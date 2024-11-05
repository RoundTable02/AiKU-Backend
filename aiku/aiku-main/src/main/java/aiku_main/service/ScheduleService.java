package aiku_main.service;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.application_event.domain.ScheduleArrivalResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.*;
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
import common.domain.Status;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import common.domain.value_reference.TeamValue;
import common.exception.BaseExceptionImpl;
import common.exception.JsonParseException;
import common.exception.NotEnoughPoint;
import common.kafka_message.alarm.*;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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

    @Transactional
    public Long addSchedule(Long memberId, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        Member member = findMember(memberId);
        Team team = findTeamById(teamId);
        checkTeamMember(memberId, teamId);
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        //서비스 로직
        Schedule schedule = Schedule.create(member, new TeamValue(team),
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation().toDomain(),
                scheduleDto.getPointAmount());
        scheduleQueryRepository.save(schedule);

        if(scheduleDto.getPointAmount() > 0) {
            pointChangeEventPublisher.publish(member, MINUS, scheduleDto.getPointAmount(), SCHEDULE_ENTER, schedule.getId());
        }

        scheduleScheduler.reserveSchedule(schedule);

        sendMessageToTeamMembers(teamId, schedule, member, AlarmMessageType.SCHEDULE_ADD);

        return schedule.getId();
    }

    private void sendMessageToTeamMembers(Long teamId, Schedule schedule, Member excludeMember, AlarmMessageType messageType){
        List<TeamMember> teamMembers = teamQueryRepository.findTeamMembersWithMemberInTeam(teamId);
        List<AlarmMemberInfo> alarmMembers = teamMembers.stream()
                .filter(teamMember -> !teamMember.getMember().getId().equals(excludeMember.getId()))
                .map(TeamMember::getMember)
                .map(AlarmMemberInfo::new)
                .toList();

        kafkaProducerService.sendMessage(alarm, new ScheduleAlarmMessage(alarmMembers, schedule, messageType));
    }

    @Transactional
    public Long updateSchedule(Long memberId, Long scheduleId, ScheduleUpdateDto scheduleDto){
        //검증 로직
        Member member = findMember(memberId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsWait(schedule);
        checkScheduleUpdateTime(schedule);
        checkIsOwner(memberId, scheduleId);

        //서비스 로직
        schedule.update(scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.location.toDomain());

        scheduleScheduler.changeSchedule(schedule);

        sendMessageToScheduleMembers(schedule, member, null, AlarmMessageType.SCHEDULE_UPDATE);

        return schedule.getId();
    }

    @Transactional
    public Long enterSchedule(Long memberId, Long teamId, Long scheduleId, ScheduleEnterDto enterDto) {
        //검증 로직
        Member member = findMember(memberId);
        checkExistTeam(teamId);
        checkTeamMember(member.getId(), teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsWait(schedule);
        checkEnoughPoint(member, enterDto.getPointAmount());
        checkScheduleMember(member.getId(), scheduleId, false);

        //서비스 로직
        schedule.addScheduleMember(member, false, enterDto.getPointAmount());

        if(enterDto.getPointAmount() > 0) {
            pointChangeEventPublisher.publish(member, MINUS, enterDto.getPointAmount(), SCHEDULE_ENTER, scheduleId);
        }

        sendMessageToScheduleMembers(schedule, member, member, AlarmMessageType.SCHEDULE_ENTER);

        return schedule.getId();
    }

    @Transactional
    public Long exitSchedule(Long memberId, Long teamId, Long scheduleId) {
        //검증 로직
        Member member = findMember(memberId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsWait(schedule);
        checkScheduleMember(memberId, scheduleId, true);

        //서비스 로직
        ScheduleMember scheduleMember = scheduleQueryRepository.findScheduleMember(memberId, scheduleId).orElseThrow();

        Long scheduleMemberCount = scheduleQueryRepository.countOfScheduleMembers(scheduleId);
        if(scheduleMemberCount <= 1){
            schedule.delete();
        }else if(scheduleMember.isOwner()){
            ScheduleMember nextScheduleOwner = findNextScheduleOwnerWithMember(scheduleId, scheduleMember.getId());
            schedule.changeScheduleOwner(nextScheduleOwner);

            Member nextMember = nextScheduleOwner.getMember();
            kafkaProducerService.sendMessage(alarm, new ScheduleAlarmMessage(
                    List.of(new AlarmMemberInfo(nextMember)), schedule, AlarmMessageType.SCHEDULE_OWNER));
        }

        schedule.removeScheduleMember(scheduleMember);

        int schedulePoint = scheduleMember.getPointAmount();
        if(schedulePoint > 0){
            pointChangeEventPublisher.publish(member, PLUS, schedulePoint, SCHEDULE_EXIT, schedule.getId());
        }

        scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);

        sendMessageToScheduleMembers(schedule, member, member, AlarmMessageType.SCHEDULE_EXIT);

        return schedule.getId();
    }

    private ScheduleMember findNextScheduleOwnerWithMember(Long scheduleId, Long scheduleMemberId){
        ScheduleMember nextOwner = scheduleQueryRepository.findNextScheduleOwnerWithMember(scheduleId, scheduleMemberId).orElse(null);
        if (nextOwner == null) {
            throw new ScheduleException(CAN_NOT_FIND_NEXT_SCHEDULE_OWNER);
        }
        return nextOwner;
    }

    private void sendMessageToScheduleMembers(Schedule schedule, Member excludeMember, Member sourceMember, AlarmMessageType messageType) {
        List<ScheduleMember> scheduleMembers = scheduleQueryRepository.findScheduleMembersWithMember(schedule.getId());
        List<AlarmMemberInfo> alarmMembers = scheduleMembers.stream()
                .filter(scheduleMember -> excludeMember == null || !scheduleMember.getMember().getId().equals(excludeMember.getId()))
                .map(ScheduleMember::getMember)
                .map(AlarmMemberInfo::new)
                .toList();

        if(sourceMember == null) {
            kafkaProducerService.sendMessage(alarm, new ScheduleAlarmMessage(alarmMembers, schedule, messageType));
        } else {
            AlarmMemberInfo sourceMemberInfo = new AlarmMemberInfo(sourceMember);
            kafkaProducerService.sendMessage(alarm, new ScheduleMemberAlarmMessage(sourceMemberInfo, alarmMembers, schedule, messageType));
        }
    }

    @Transactional
    public void arriveSchedule(Long scheduleId, Long memberId, LocalDateTime arrivalTime){
        Schedule schedule = findScheduleById(scheduleId);
        ScheduleMember scheduleMember = scheduleQueryRepository.findScheduleMember(memberId, scheduleId).orElseThrow();

        schedule.arriveScheduleMember(scheduleMember, arrivalTime);
    }

    @Transactional
    public void closeSchedule(Long scheduleId, LocalDateTime scheduleCloseTime){
        Schedule schedule = findScheduleById(scheduleId);
        schedule.close(scheduleCloseTime);

        scheduleEventPublisher.publishScheduleCloseEvent(schedule);
    }

    public ScheduleDetailResDto getScheduleDetail(Long memberId, Long teamId, Long scheduleId) {
        //검증 메서드
        Schedule schedule = findScheduleById(scheduleId);
        checkScheduleMember(memberId, scheduleId, true);

        //서비스 로직
        List<ScheduleMemberResDto> membersDtoList = scheduleQueryRepository.getScheduleMembersWithBettingInfo(memberId, scheduleId);

        return new ScheduleDetailResDto(schedule, membersDtoList);
    }

    public TeamScheduleListResDto getTeamScheduleList(Long memberId, Long teamId, SearchDateCond dateCond, int page) {
        //검증 메서드
        Team team = findTeamById(teamId);
        checkTeamMember(memberId, teamId);

        //서비스 로직
        List<TeamScheduleListEachResDto> scheduleList = scheduleQueryRepository.getTeamSchedules(teamId, memberId, dateCond, page);
        scheduleList.forEach((schedule) -> schedule.setAccept(memberId));
        int runSchedule = scheduleQueryRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleQueryRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.WAIT, dateCond);

        return new TeamScheduleListResDto(team, page, runSchedule, waitSchedule, scheduleList);
    }

    public MemberScheduleListResDto getMemberScheduleList(Long memberId, SearchDateCond dateCond, int page) {
        //서비스 로직
        List<MemberScheduleListEachResDto> scheduleList = scheduleQueryRepository.getMemberSchedules(memberId, dateCond, page);
        int runSchedule = scheduleQueryRepository.countMemberScheduleByScheduleStatus(memberId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleQueryRepository.countMemberScheduleByScheduleStatus(memberId, ExecStatus.WAIT, dateCond);

        return new MemberScheduleListResDto(page, runSchedule, waitSchedule, scheduleList);
    }

    public String getScheduleArrivalResult(Long memberId, Long teamId, Long scheduleId) {
        //검증 로직
        checkExistTeam(teamId);
        checkTeamMember(memberId, teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsTerm(schedule);

        //서비스 로직
        return schedule.getScheduleResult() == null? null : schedule.getScheduleResult().getScheduleArrivalResult();
    }

    public String getScheduleBettingResult(Long memberId, Long teamId, Long scheduleId) {
        //검증 로직
        checkExistTeam(teamId);
        checkTeamMember(memberId, teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsTerm(schedule);

        //서비스 로직
        return schedule.getScheduleResult() == null? null : schedule.getScheduleResult().getScheduleBettingResult();
    }

    public String getScheduleRacingResult(Long memberId, Long teamId, Long scheduleId) {
        checkExistTeam(teamId);
        checkTeamMember(memberId, teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsTerm(schedule);

        return schedule.getScheduleResult() == null? null : schedule.getScheduleResult().getScheduleRacingResult();
    }

    //== 이벤트 핸들러 ==
    @Transactional
    public void exitAllScheduleInTeam(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        List<ScheduleMember> scheduleMembers = scheduleQueryRepository.findWaitScheduleMemberWithScheduleInTeam(memberId, teamId);
        scheduleMembers.forEach((scheduleMember) ->{
            Schedule schedule = scheduleMember.getSchedule();
            schedule.removeScheduleMember(scheduleMember);

            if(scheduleMember.getPointAmount() > 0){
                pointChangeEventPublisher.publish(member, PLUS, scheduleMember.getPointAmount(), SCHEDULE_EXIT, schedule.getId());
            }

            scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);

            sendMessageToScheduleMembers(schedule, member, member, AlarmMessageType.SCHEDULE_EXIT);
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
        if (scheduleQueryRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, ExecStatus.TERM, Status.ALIVE)){
            return;
        }

        Schedule schedule = findScheduleById(scheduleId);
        List<ScheduleMember> notArriveScheduleMembers = scheduleQueryRepository.findNotArriveScheduleMember(scheduleId);

        LocalDateTime autoCloseTime = schedule.getScheduleTime().plusMinutes(30);
        schedule.autoClose(notArriveScheduleMembers, autoCloseTime);

        sendMessageToScheduleMembers(schedule, null, null, AlarmMessageType.SCHEDULE_AUTO_CLOSE);
    }

    @Transactional
    public void processScheduleResultPoint(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();

        List<ScheduleMember> earlyMembers = scheduleQueryRepository.findPaidEarlyScheduleMemberWithMember(scheduleId);

        if(earlyMembers.size() == 0) {
            scheduleQueryRepository.findPaidLateScheduleMemberWithMember(scheduleId)
                    .forEach((lateMember) -> {
                        int rewardPointAmount = lateMember.getPointAmount();
                        pointChangeEventPublisher.publish(lateMember.getMember(), PLUS, rewardPointAmount, SCHEDULE_REWARD, scheduleId);
                        schedule.rewardMember(lateMember, rewardPointAmount);
                    });
            return;
        }

        int pointAmountOfLateMembers = scheduleQueryRepository.findPointAmountOfLatePaidScheduleMember(scheduleId);
        int rewardOfEarlyMember = pointAmountOfLateMembers / earlyMembers.size();

        earlyMembers.forEach((earlyScheduleMember) -> {
            int rewardPointAmount = earlyScheduleMember.getPointAmount() + rewardOfEarlyMember;
            pointChangeEventPublisher.publish(earlyScheduleMember.getMember(), PLUS, rewardPointAmount, SCHEDULE_REWARD, scheduleId);
            schedule.rewardMember(earlyScheduleMember, rewardPointAmount);
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
        } ;
    }

    public boolean isScheduleAutoClosed(Long scheduleId){
        return scheduleQueryRepository.existsByIdAndIsAutoClose(scheduleId, true);
    }

    private Member findMember(Long memberId){
        return memberRepository.findById(memberId).orElseThrow();
    }

    private Team findTeamById(Long teamId){
        Team team = teamQueryRepository.findByIdAndStatus(teamId, ALIVE).orElse(null);
        if (team == null) {
            throw new TeamException(NO_SUCH_TEAM);
        }
        return team;
    }

    private Schedule findScheduleById(Long scheduleId){
        Schedule schedule = scheduleQueryRepository.findByIdAndStatus(scheduleId, ALIVE).orElse(null);
        if (schedule == null) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
        return schedule;
    }

    private void checkExistTeam(Long teamId){
        if(!teamQueryRepository.existsById(teamId)){
            throw new TeamException(NO_SUCH_TEAM);
        }
    }

    private void checkIsWait(Schedule schedule){
        if(schedule.getScheduleStatus() != ExecStatus.WAIT){
            throw new ScheduleException(NO_WAIT_SCHEDULE);
        }
    }

    private void checkIsTerm(Schedule schedule){
        if(schedule.getScheduleStatus() != ExecStatus.TERM){
            throw new BaseExceptionImpl(BaseErrorCode.NO_TERM_SCHEDULE);
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
