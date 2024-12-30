package aiku_main.service;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.application_event.domain.ScheduleArrivalResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.*;
import aiku_main.dto.schedule.*;
import aiku_main.exception.ScheduleException;
import aiku_main.exception.TeamException;
import aiku_main.kafka.KafkaProducerService;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleQueryRepository;
import aiku_main.repository.TeamQueryRepository;
import aiku_main.scheduler.ScheduleScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.ExecStatus;
import common.domain.schedule.ScheduleMember;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleResult;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import common.exception.JsonParseException;
import common.exception.NotEnoughPoint;
import common.kafka_message.alarm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static aiku_main.application_event.event.PointChangeReason.*;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;
import static common.domain.Status.ALIVE;
import static common.kafka_message.KafkaTopic.alarm;
import static common.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final MemberRepository memberRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final TeamQueryRepository teamQueryRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;
    private final ScheduleEventPublisher scheduleEventPublisher;
    private final ScheduleScheduler scheduleScheduler;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Value("${schedule.fee.participation}")
    private int scheduleEnterPoint;

    @Transactional
    public Long addSchedule(Long memberId, Long teamId, ScheduleAddDto scheduleDto){
        Member member = findMember(memberId);
        checkTeamMember(memberId, teamId);
        checkEnoughPoint(member, scheduleEnterPoint);

        Schedule schedule = Schedule.create(
                member,
                new TeamValue(teamId),
                scheduleDto.getScheduleName(),
                scheduleDto.getScheduleTime(),
                scheduleDto.getLocation().toDomain(),
                scheduleEnterPoint);
        scheduleQueryRepository.save(schedule);

        scheduleScheduler.reserveSchedule(schedule);
        pointChangeEventPublisher.publish(member, MINUS, scheduleEnterPoint, SCHEDULE_ENTER, schedule.getId());
        sendMessageToTeamMembers(teamId, schedule, member.getId(), AlarmMessageType.SCHEDULE_ADD);

        return schedule.getId();
    }

    private void sendMessageToTeamMembers(Long teamId, Schedule schedule, Long excludeMemberId, AlarmMessageType messageType){
        List<String> alarmTokens = teamQueryRepository.findAlarmTokenListOfTeamMembers(teamId, excludeMemberId);
        kafkaProducerService.sendMessage(alarm, new ScheduleAlarmMessage(alarmTokens, messageType, schedule));
    }

    @Transactional
    public Long updateSchedule(Long memberId, Long scheduleId, ScheduleUpdateDto scheduleDto){
        Schedule schedule = findSchedule(scheduleId);
        checkIsWait(schedule);
        checkScheduleUpdateTime(schedule);
        checkIsOwner(memberId, scheduleId);

        schedule.update(scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.location.toDomain());

        scheduleScheduler.changeSchedule(schedule);
        sendMessageToScheduleMembers(schedule, memberId, null, AlarmMessageType.SCHEDULE_UPDATE);

        return schedule.getId();
    }

    @Transactional
    public Long enterSchedule(Long memberId, Long teamId, Long scheduleId) {
        checkTeamMember(memberId, teamId);

        Member member = findMember(memberId);
        checkEnoughPoint(member, scheduleEnterPoint);

        Schedule schedule = findSchedule(scheduleId);
        checkScheduleMember(memberId, scheduleId, false);
        checkIsWait(schedule);

        schedule.addScheduleMember(member, false, scheduleEnterPoint);

        sendMessageToScheduleMembers(schedule, memberId, member, AlarmMessageType.SCHEDULE_ENTER);
        pointChangeEventPublisher.publish(member, MINUS, scheduleEnterPoint, SCHEDULE_ENTER, scheduleId);

        return schedule.getId();
    }

    @Transactional
    public Long exitSchedule(Long memberId, Long teamId, Long scheduleId) {
        Member member = findMember(memberId);
        ScheduleMember scheduleMember = findScheduleMember(memberId, scheduleId);
        Schedule schedule = findSchedule(scheduleId);
        checkIsWait(schedule);

        Long scheduleMemberCount = scheduleQueryRepository.countOfScheduleMembers(scheduleId);
        if(scheduleMemberCount <= 1){
            schedule.delete();
        }else if(scheduleMember.isOwner()){
            ScheduleMember nextScheduleOwner = findNextScheduleOwnerWithMember(scheduleId, scheduleMember.getId());
            schedule.changeScheduleOwner(nextScheduleOwner);

            sendMessageToScheduleMember(schedule, nextScheduleOwner.getMember().getFirebaseToken(), AlarmMessageType.SCHEDULE_OWNER);
        }

        schedule.removeScheduleMember(scheduleMember);

        sendMessageToScheduleMembers(schedule, memberId, member, AlarmMessageType.SCHEDULE_EXIT);
        pointChangeEventPublisher.publish(member, PLUS, scheduleEnterPoint, SCHEDULE_EXIT, schedule.getId());
        scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);

        return schedule.getId();
    }

    private ScheduleMember findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleId){
        return scheduleQueryRepository.findNextScheduleOwnerWithMember(scheduleId, prevOwnerScheduleId)
                .orElseThrow(() -> new ScheduleException(CAN_NOT_FIND_NEXT_SCHEDULE_OWNER));
    }

    private void sendMessageToScheduleMembers(Schedule schedule, Long excludeMemberId, Member sourceMember, AlarmMessageType messageType) {
        List<String> alarmTokens = scheduleQueryRepository.findAlarmTokenListOfScheduleMembers(schedule.getId(), excludeMemberId);

        if(sourceMember == null) {
            kafkaProducerService.sendMessage(
                    alarm,
                    new ScheduleAlarmMessage(alarmTokens, messageType, schedule)
            );
        } else {
            kafkaProducerService.sendMessage(
                    alarm,
                    new ScheduleMemberAlarmMessage(alarmTokens, messageType, new AlarmMemberInfo(sourceMember), schedule)
            );
        }
    }

    private void sendMessageToScheduleMember(Schedule schedule, String alarmToken, AlarmMessageType messageType){
        kafkaProducerService.sendMessage(
                alarm,
                new ScheduleAlarmMessage(List.of(alarmToken), messageType, schedule)
        );
    }

    @Transactional
    public void arriveSchedule(Long scheduleId, Long memberId, LocalDateTime arrivalTime){
        Schedule schedule = findSchedule(scheduleId);
        ScheduleMember scheduleMember = scheduleQueryRepository.findScheduleMember(memberId, scheduleId).orElseThrow();

        schedule.arriveScheduleMember(scheduleMember, arrivalTime);
    }

    @Transactional
    public void closeSchedule(Long scheduleId, LocalDateTime scheduleCloseTime){
        Schedule schedule = findSchedule(scheduleId);
        schedule.close(scheduleCloseTime);

        scheduleEventPublisher.publishScheduleCloseEvent(schedule);
    }

    public ScheduleDetailResDto getScheduleDetail(Long memberId, Long teamId, Long scheduleId) {
        Schedule schedule = findSchedule(scheduleId);
        checkScheduleMember(memberId, scheduleId, true);

        List<ScheduleMemberResDto> membersDtoList = scheduleQueryRepository.getScheduleMembersWithBettingInfo(memberId, scheduleId);

        return new ScheduleDetailResDto(schedule, membersDtoList);
    }

    public TeamScheduleListResDto getTeamScheduleList(Long memberId, Long teamId, SearchDateCond dateCond, int page) {
        Team team = findTeam(teamId);
        checkTeamMember(memberId, teamId);

        List<TeamScheduleListEachResDto> scheduleList = scheduleQueryRepository.getTeamSchedules(teamId, memberId, dateCond, page);
        scheduleList.forEach((schedule) -> schedule.setAccept(memberId));
        int runSchedule = scheduleQueryRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleQueryRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.WAIT, dateCond);

        return new TeamScheduleListResDto(team, page, runSchedule, waitSchedule, scheduleList);
    }

    public MemberScheduleListResDto getMemberScheduleList(Long memberId, SearchDateCond dateCond, int page) {
        List<MemberScheduleListEachResDto> scheduleList = scheduleQueryRepository.getMemberSchedules(memberId, dateCond, page);
        int runSchedule = scheduleQueryRepository.countMemberScheduleByScheduleStatus(memberId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleQueryRepository.countMemberScheduleByScheduleStatus(memberId, ExecStatus.WAIT, dateCond);

        return new MemberScheduleListResDto(page, runSchedule, waitSchedule, scheduleList);
    }

    public String getScheduleArrivalResult(Long memberId, Long teamId, Long scheduleId) {
        checkTeamMember(memberId, teamId);
        ScheduleResult scheduleResult = findScheduleResult(scheduleId);

        return scheduleResult.getScheduleArrivalResult();
    }

    public String getScheduleBettingResult(Long memberId, Long teamId, Long scheduleId) {
        checkTeamMember(memberId, teamId);
        ScheduleResult scheduleResult = findScheduleResult(scheduleId);

        return scheduleResult.getScheduleBettingResult();
    }

    public String getScheduleRacingResult(Long memberId, Long teamId, Long scheduleId) {
        checkTeamMember(memberId, teamId);
        ScheduleResult scheduleResult = findScheduleResult(scheduleId);

        return scheduleResult.getScheduleRacingResult();
    }

    public SimpleResDto<List<LocalDate>> getScheduleDatesInMonth(Long memberId, MonthDto monthDto) {
        List<LocalDate> scheduleTimeList = scheduleQueryRepository.findScheduleDatesInMonth(memberId, monthDto.getYear(), monthDto.getMonth())
                .stream()
                .map(dateTime -> dateTime.toLocalDate())
                .distinct()
                .toList();

        return new SimpleResDto<>(scheduleTimeList);
    }

    //== 이벤트 핸들러 ==
    @Transactional
    public void exitAllScheduleInTeam(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        List<ScheduleMember> scheduleMembers = scheduleQueryRepository.findWaitScheduleMemberWithScheduleInTeam(memberId, teamId);
        scheduleMembers.forEach((scheduleMember) ->{
            Schedule schedule = scheduleMember.getSchedule();
            schedule.removeScheduleMember(scheduleMember);

            pointChangeEventPublisher.publish(member, PLUS, scheduleEnterPoint, SCHEDULE_EXIT, schedule.getId());
            scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);
            sendMessageToScheduleMembers(schedule, member.getId(), member, AlarmMessageType.SCHEDULE_EXIT);
        });
    }

    @Transactional
    public void openSchedule(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        schedule.setRun();

        sendMessageToScheduleMembers(schedule, null, null, AlarmMessageType.SCHEDULE_OPEN);
    }

    @Transactional
    public void closeScheduleAuto(Long scheduleId) {
        Schedule schedule = findSchedule(scheduleId);
        if (schedule.getScheduleStatus() == ExecStatus.TERM){
            return;
        }

        List<ScheduleMember> notArriveScheduleMembers = scheduleQueryRepository.findNotArriveScheduleMember(scheduleId);

        LocalDateTime autoCloseTime = schedule.getScheduleTime().plusMinutes(30);
        schedule.autoClose(notArriveScheduleMembers, autoCloseTime);

        sendMessageToScheduleMembers(schedule, null, null, AlarmMessageType.SCHEDULE_AUTO_CLOSE);
        scheduleEventPublisher.publishScheduleCloseEvent(schedule);
    }

    @Transactional
    public void processScheduleResultPoint(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();

        List<ScheduleMember> earlyMembers = scheduleQueryRepository.findEarlyScheduleMemberWithMember(scheduleId);

        if(earlyMembers.isEmpty()) {
            refundScheduleEnterPointToMembers(schedule);
            return;
        }

        int lateScheduleMemberCount = scheduleQueryRepository.findLateScheduleMemberCount(scheduleId);
        int rewardOfEarlyMember = lateScheduleMemberCount * scheduleEnterPoint / earlyMembers.size();
        int rewardPointAmount = scheduleEnterPoint + rewardOfEarlyMember;

        earlyMembers.forEach((earlyScheduleMember) -> {
            schedule.rewardMember(earlyScheduleMember, rewardPointAmount);
            pointChangeEventPublisher.publish(earlyScheduleMember.getMember(), PLUS, rewardPointAmount, SCHEDULE_REWARD, scheduleId);
        });
    }

    private void refundScheduleEnterPointToMembers(Schedule schedule){
        scheduleQueryRepository.findScheduleMemberListWithMember(schedule.getId())
                .forEach((scheduleMember) -> {
                    schedule.rewardMember(scheduleMember, scheduleEnterPoint);
                    pointChangeEventPublisher.publish(scheduleMember.getMember(), PLUS, scheduleEnterPoint, SCHEDULE_REWARD, schedule.getId());
                });
    }

    @Transactional
    public void analyzeScheduleArrivalResult(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();

        List<ScheduleArrivalMember> arrivalMembers = scheduleQueryRepository.getScheduleArrivalResults(scheduleId);
        ScheduleArrivalResult arrivalResult = new ScheduleArrivalResult(scheduleId, arrivalMembers);
        try {
            schedule.setScheduleArrivalResult(objectMapper.writeValueAsString(arrivalResult));
        } catch (JsonProcessingException e) {
            throw new JsonParseException();
        }
    }

    private Member findMember(Long memberId){
        return memberRepository.findById(memberId).orElseThrow();
    }

    private Team findTeam(Long teamId){
        return teamQueryRepository.findByIdAndStatus(teamId, ALIVE)
                .orElseThrow(() -> new TeamException(NO_SUCH_TEAM));
    }

    private Schedule findSchedule(Long scheduleId){
        return scheduleQueryRepository.findByIdAndStatus(scheduleId, ALIVE)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId){
        return scheduleQueryRepository.findScheduleMember(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NOT_IN_SCHEDULE));
    }

    private ScheduleResult findScheduleResult(Long scheduleId){
        return scheduleQueryRepository.findScheduleResult(scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE_RESULT));
    }

    private void checkIsWait(Schedule schedule){
        if(schedule.getScheduleStatus() != ExecStatus.WAIT){
            throw new ScheduleException(NO_WAIT_SCHEDULE);
        }
    }

    private void checkTeamMember(Long memberId, Long teamId){
        if(!teamQueryRepository.existTeamMember(memberId, teamId)){
            throw new TeamException(NOT_IN_TEAM);
        }
    }

    private void checkScheduleMember(Long memberId, Long scheduleId, boolean isMember){
        if(scheduleQueryRepository.existScheduleMember(memberId, scheduleId) != isMember){
            if(isMember){
                throw new ScheduleException(NOT_IN_SCHEDULE);
            }else{
                throw new ScheduleException(ALREADY_IN_SCHEDULE);
            }
        }
    }

    private void checkIsOwner(Long memberId, Long scheduleId){
        if(!scheduleQueryRepository.isScheduleOwner(memberId, scheduleId)){
            throw new ScheduleException(NO_SCHEDULE_OWNER);
        }
    }

    private void checkEnoughPoint(Member member, int point){
        if(member.getPoint() < point){
            throw new NotEnoughPoint();
        }
    }

    private void checkScheduleUpdateTime(Schedule schedule){
        long minutesDiff = Duration.between(LocalDateTime.now(), schedule.getScheduleTime()).toMinutes();
        if(minutesDiff < 60){
            throw new ScheduleException(FORBIDDEN_SCHEDULE_UPDATE_TIME);
        }
    }
}
