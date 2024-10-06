package aiku_main.service;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.application_event.domain.ScheduleArrivalResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.*;
import aiku_main.exception.ScheduleException;
import aiku_main.exception.TeamException;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleReadRepository;
import aiku_main.repository.ScheduleRepository;
import aiku_main.repository.TeamRepository;
import aiku_main.scheduler.ScheduleScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.ExecStatus;
import common.domain.schedule.ScheduleMember;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.Status;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import common.exception.BaseExceptionImpl;
import common.exception.NotEnoughPoint;
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
import static common.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleReadRepository scheduleReadRepository;
    private final TeamRepository teamRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;
    private final ScheduleEventPublisher scheduleEventPublisher;
    private final ScheduleScheduler scheduleScheduler;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long addSchedule(Member member, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId);
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        //서비스 로직
        Schedule schedule = Schedule.create(member, new TeamValue(team),
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation().toDomain(),
                scheduleDto.getPointAmount());
        scheduleRepository.save(schedule);

        pointChangeEventPublisher.publish(member, MINUS, scheduleDto.getPointAmount(), SCHEDULE_ENTER, schedule.getId());
        scheduleScheduler.reserveSchedule(schedule);

        return schedule.getId();
    }

    @Transactional
    public Long updateSchedule(Member member, Long scheduleId, ScheduleUpdateDto scheduleDto){
        //검증 로직
        Schedule schedule = findScheduleById(scheduleId);
        checkIsWait(schedule);
        checkScheduleUpdateTime(schedule);
        checkIsOwner(member.getId(), scheduleId);

        //서비스 로직
        schedule.update(scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.location.toDomain());

        scheduleScheduler.changeSchedule(schedule);

        return schedule.getId();
    }

    @Transactional
    public Long enterSchedule(Member member, Long teamId, Long scheduleId, ScheduleEnterDto enterDto) {
        //검증 로직
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
        };

        return schedule.getId();
    }

    @Transactional
    public Long exitSchedule(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        Schedule schedule = findScheduleById(scheduleId);
        checkIsWait(schedule);
        checkScheduleMember(member.getId(), scheduleId, true);

        //서비스 로직
        ScheduleMember scheduleMember = scheduleRepository.findAliveScheduleMember(member.getId(), scheduleId).orElseThrow();

        Long scheduleMemberCount = scheduleRepository.countOfAliveScheduleMember(scheduleId);
        if(scheduleMemberCount <= 1){
            schedule.delete();
        }else if(scheduleMember.isOwner()){
            ScheduleMember nextScheduleOwner = findNextScheduleOwner(scheduleId, scheduleMember.getId());
            schedule.changeScheduleOwner(nextScheduleOwner);
        }

        schedule.removeScheduleMember(scheduleMember);

        int schedulePoint = scheduleMember.getPointAmount();
        if(schedulePoint > 0){
            pointChangeEventPublisher.publish(member, PLUS, schedulePoint, SCHEDULE_EXIT, schedule.getId());
        }

        scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);

        return schedule.getId();
    }

    //== 조회 서비스 ==
    public ScheduleDetailResDto getScheduleDetail(Member member, Long teamId, Long scheduleId) {
        //검증 메서드
        Schedule schedule = findScheduleById(scheduleId);
        checkScheduleMember(member.getId(), scheduleId, true);

        //서비스 로직
        List<ScheduleMemberResDto> membersDtoList = scheduleReadRepository.getScheduleMembersWithMember(scheduleId);

        return new ScheduleDetailResDto(schedule, membersDtoList);
    }

    public TeamScheduleListResDto getTeamScheduleList(Member member, Long teamId, SearchDateCond dateCond, int page) {
        //검증 메서드
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId);

        //서비스 로직
        List<TeamScheduleListEachResDto> scheduleList = scheduleReadRepository.getTeamScheduleList(teamId, member.getId(), dateCond, page);
        scheduleList.forEach((schedule) -> schedule.setAccept(member.getId()));
        int runSchedule = scheduleReadRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleReadRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.WAIT, dateCond);

        return new TeamScheduleListResDto(team, page, runSchedule, waitSchedule, scheduleList);
    }

    public MemberScheduleListResDto getMemberScheduleList(Member member, SearchDateCond dateCond, int page) {
        //서비스 로직
        List<MemberScheduleListEachResDto> scheduleList = scheduleReadRepository.getMemberScheduleList(member.getId(), dateCond, page);
        int runSchedule = scheduleReadRepository.countMemberScheduleByScheduleStatus(member.getId(), ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleReadRepository.countMemberScheduleByScheduleStatus(member.getId(), ExecStatus.WAIT, dateCond);

        return new MemberScheduleListResDto(page, runSchedule, waitSchedule, scheduleList);
    }

    public String getScheduleArrivalResult(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        checkExistTeam(teamId);
        checkTeamMember(member.getId(), teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsTerm(schedule);

        //서비스 로직
        return schedule.getScheduleResult().getScheduleArrivalResult();
    }

    public String getScheduleBettingResult(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        checkExistTeam(teamId);
        checkTeamMember(member.getId(), teamId);
        Schedule schedule = findScheduleById(scheduleId);
        checkIsTerm(schedule);

        //서비스 로직
        return schedule.getScheduleResult().getScheduleBettingResult();
    }

    //== 이벤트 핸들러 ==
    @Transactional
    public void exitAllScheduleInTeam(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        List<ScheduleMember> scheduleMembers = scheduleRepository.findWaitScheduleMemberWithScheduleInTeam(memberId, teamId);
        scheduleMembers.forEach((scheduleMember) ->{
            Schedule schedule = scheduleMember.getSchedule();
            schedule.removeScheduleMember(scheduleMember);
            scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);
        });
    }

    @Transactional
    public void openSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setRun();

        //TODO 카프카, 알림
    }

    @Transactional
    public void closeScheduleAuto(Long scheduleId) {
        if (scheduleRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, ExecStatus.TERM, Status.ALIVE)){
            return;
        }

        Schedule schedule = scheduleRepository.findScheduleWithNotArriveScheduleMember(scheduleId).orElseThrow();

        LocalDateTime autoCloseTime = schedule.getScheduleTime().plusMinutes(30);
        schedule.autoClose(schedule.getScheduleMembers(), autoCloseTime);
    }

    @Transactional
    public void processScheduleResultPoint(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        List<ScheduleMember> earlyMembers = scheduleRepository.findPaidEarlyScheduleMemberWithMember(scheduleId);

        if(earlyMembers.size() == 0) {
            scheduleRepository.findPaidLateScheduleMemberWithMember(scheduleId)
                    .forEach((lateMember) -> {
                        int rewardPointAmount = lateMember.getPointAmount();
                        pointChangeEventPublisher.publish(lateMember.getMember(), PLUS, rewardPointAmount, SCHEDULE_REWARD, scheduleId);
                        schedule.rewardMember(lateMember, rewardPointAmount);
                    });
            return;
        }

        int pointAmountOfLateMembers = scheduleRepository.findPointAmountOfLatePaidScheduleMember(scheduleId);
        int rewardOfEarlyMember = pointAmountOfLateMembers / earlyMembers.size();

        earlyMembers.forEach((earlyScheduleMember) -> {
            int rewardPointAmount = earlyScheduleMember.getPointAmount() + rewardOfEarlyMember;
            pointChangeEventPublisher.publish(earlyScheduleMember.getMember(), PLUS, rewardPointAmount, SCHEDULE_REWARD, scheduleId);
            schedule.rewardMember(earlyScheduleMember, rewardPointAmount);
        });
    }

    @Transactional
    public void analyzeScheduleArrivalResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        List<ScheduleArrivalMember> arrivalMembers = scheduleReadRepository.getScheduleArrivalResults(scheduleId);
        ScheduleArrivalResult arrivalResult = new ScheduleArrivalResult(scheduleId, arrivalMembers);
        try {
            schedule.setScheduleArrivalResult(objectMapper.writeValueAsString(arrivalResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't Parse ScheduleArrivalResult");
        } ;
    }

    public boolean isScheduleAutoClosed(Long scheduleId){
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        return schedule.isAutoClose();
    }

    //==* 기타 메서드 *==
    private Team findTeamById(Long teamId){
        Team team = teamRepository.findByIdAndStatus(teamId, ALIVE).orElse(null);
        if (team == null) {
            throw new TeamException(NO_SUCH_TEAM);
        }
        return team;
    }

    private Schedule findScheduleById(Long scheduleId){
        Schedule schedule = scheduleRepository.findByIdAndStatus(scheduleId, ALIVE).orElse(null);
        if (schedule == null) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
        return schedule;
    }

    private void checkExistTeam(Long teamId){
        if(!teamRepository.existsById(teamId)){
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
        if(!teamRepository.existTeamMember(memberId, teamId)){
            throw new TeamException(NOT_IN_TEAM);
        }
    }

    private void checkScheduleMember(Long memberId, Long scheduleId, boolean isMember){
        if(scheduleRepository.existScheduleMember(memberId, scheduleId) != isMember){
            if(isMember){
                throw new ScheduleException(NOT_IN_SCHEDULE);
            }else{
                throw new ScheduleException(ALREADY_IN_SCHEDULE);
            }
        }
    }

    private void checkIsOwner(Long memberId, Long scheduleId){
        if(!scheduleRepository.isScheduleOwner(memberId, scheduleId)){
            throw new ScheduleException(NO_SCHEDULE_OWNER);
        }
    }

    private ScheduleMember findNextScheduleOwner(Long scheduleId, Long scheduleMemberId){
        ScheduleMember nextOwner = scheduleRepository.findNextScheduleOwner(scheduleId, scheduleMemberId).orElse(null);
        if (nextOwner == null) {
            throw new ScheduleException(CAN_NOT_FIND_NEXT_SCHEDULE_OWNER);
        }
        return nextOwner;
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
