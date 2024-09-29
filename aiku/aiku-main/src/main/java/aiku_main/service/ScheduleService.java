package aiku_main.service;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.application_event.domain.ScheduleArrivalResult;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.*;
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
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static aiku_main.application_event.event.PointChangeReason.SCHEDULE;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;

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

    //TODO 카프카를 통한 푸시 알림 로직 추가해야됨
    @Transactional
    public Long addSchedule(Member member, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        checkTeamMember(member.getId(), teamId);
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        Team team = teamRepository.findById(teamId).orElseThrow();
        checkIsAlive(team);

        //서비스 로직
        Schedule schedule = Schedule.create(member, new TeamValue(team),
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation().toDomain(),
                scheduleDto.getPointAmount());
        scheduleRepository.save(schedule);

        pointChangeEventPublisher.publish(member, MINUS, scheduleDto.getPointAmount(), SCHEDULE, schedule.getId());
        scheduleScheduler.reserveSchedule(schedule);

        return schedule.getId();
    }

    //TODO 카프카를 통한 푸시 알림 로직 추가해야됨
    @Transactional
    public Long updateSchedule(Member member, Long scheduleId, ScheduleUpdateDto scheduleDto){
        //검증 로직
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);
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
        checkTeamMember(member.getId(), teamId);
        checkEnoughPoint(member, enterDto.getPointAmount());
        checkScheduleMember(member.getId(), scheduleId, false);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);
        checkIsWait(schedule);

        //서비스 로직
        schedule.addScheduleMember(member, false, enterDto.getPointAmount());

        if(enterDto.getPointAmount() > 0) {
            pointChangeEventPublisher.publish(member, MINUS, enterDto.getPointAmount(), SCHEDULE, scheduleId);
        };

        return schedule.getId();
    }

    //팀원이 남았다면 방장 위임, 안남았다면 스케줄 삭제
    @Transactional
    public Long exitSchedule(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        checkScheduleMember(member.getId(), scheduleId, true);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);
        checkIsWait(schedule);

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
            pointChangeEventPublisher.publish(member, PLUS, schedulePoint, SCHEDULE, schedule.getId());
        }

        scheduleEventPublisher.publishScheduleExitEvent(member, scheduleMember, schedule);

        return schedule.getId();
    }

    //== 조회 서비스 ==
    public ScheduleDetailResDto getScheduleDetail(Member member, Long teamId, Long scheduleId) {
        //검증 메서드
        checkScheduleMember(member.getId(), scheduleId, true);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);

        //서비스 로직
        List<ScheduleMemberResDto> membersDtoList = scheduleReadRepository.getScheduleMembersWithMember(scheduleId);

        return new ScheduleDetailResDto(schedule, membersDtoList);
    }

    public TeamScheduleListResDto getTeamScheduleList(Member member, Long teamId, SearchDateCond dateCond, int page) {
        //검증 메서드
        checkTeamMember(member.getId(), teamId);

        Team team = teamRepository.findById(teamId).orElseThrow();
        checkIsAlive(team);

        //서비스 로직
        TotalCountDto totalCount = new TotalCountDto();
        List<TeamScheduleListEachResDto> scheduleList = scheduleReadRepository.getTeamScheduleList(teamId, member.getId(), dateCond, page, totalCount);
        scheduleList.forEach((schedule) -> schedule.setAccept(member.getId()));
        int runSchedule = scheduleReadRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleReadRepository.countTeamScheduleByScheduleStatus(teamId, ExecStatus.WAIT, dateCond);

        return new TeamScheduleListResDto(team, totalCount.getTotalCount(), page, runSchedule, waitSchedule, scheduleList);
    }

    public MemberScheduleListResDto getMemberScheduleList(Member member, SearchDateCond dateCond, int page) {
        //서비스 로직
        TotalCountDto totalCount = new TotalCountDto();
        List<MemberScheduleListEachResDto> scheduleList = scheduleReadRepository.getMemberScheduleList(member.getId(), dateCond, page, totalCount);
        int runSchedule = scheduleReadRepository.countMemberScheduleByScheduleStatus(member.getId(), ExecStatus.RUN, dateCond);
        int waitSchedule = scheduleReadRepository.countMemberScheduleByScheduleStatus(member.getId(), ExecStatus.WAIT, dateCond);

        return new MemberScheduleListResDto(totalCount.getTotalCount(), page, runSchedule, waitSchedule, scheduleList);
    }

    public String getScheduleArrivalResult(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsTerm(schedule);

        //서비스 로직
        return schedule.getScheduleResult().getScheduleArrivalResult();
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
    public void scheduleOpen(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setRun();

        //TODO 카프카, 알림
    }

    @Transactional
    public void scheduleAutoClose(Long scheduleId) {
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
                        pointChangeEventPublisher.publish(lateMember.getMember(), PLUS, rewardPointAmount, SCHEDULE, scheduleId);
                        schedule.rewardMember(lateMember, rewardPointAmount);
                    });
            return;
        }

        int pointAmountOfLateMembers = scheduleRepository.findPointAmountOfLatePaidScheduleMember(scheduleId);
        int rewardOfEarlyMember = pointAmountOfLateMembers / earlyMembers.size();

        earlyMembers.forEach((earlyScheduleMember) -> {
            int rewardPointAmount = earlyScheduleMember.getPointAmount() + rewardOfEarlyMember;
            pointChangeEventPublisher.publish(earlyScheduleMember.getMember(), PLUS, rewardPointAmount, SCHEDULE, scheduleId);
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

    //== 편의 메서드 ==
    private ScheduleMember findNextScheduleOwner(Long scheduleId, Long scheduleMemberId){
        ScheduleMember nextOwner = scheduleRepository.findNextScheduleOwner(scheduleId, scheduleMemberId).orElse(null);
        if (nextOwner == null) {
            throw new BaseExceptionImpl(BaseErrorCode.INTERNAL_SERVER_ERROR, "스케줄의 다음 방장을 찾을 수 없습니다.");
        }
        return nextOwner;
    }
    private void checkIsAlive(Schedule schedule){
        if(schedule.getStatus() == Status.DELETE){
            throw new NoSuchElementException();
        }
    }

    private void checkIsWait(Schedule schedule){
        if(schedule.getScheduleStatus() != ExecStatus.WAIT){
            throw new BaseExceptionImpl(BaseErrorCode.FORBIDDEN_SCHEDULE_UPDATE_STATUS);
        }
    }

    private void checkIsTerm(Schedule schedule){
        if(schedule.getScheduleStatus() != ExecStatus.TERM){
            throw new BaseExceptionImpl(BaseErrorCode.SCHEDULE_NOT_TERM);
        }
    }

    private void checkIsAlive(Team team){
        if(team.getStatus() == Status.DELETE){
            throw new NoSuchElementException();
        }
    }

    private void checkIsOwner(Long memberId, Long scheduleId){
        if(!scheduleRepository.isScheduleOwner(memberId, scheduleId)){
            throw new NoAuthorityException();
        }
    }

    private void checkEnoughPoint(Member member, int point){
        if(member.getPoint() < point){
            throw new NotEnoughPoint();
        }
    }

    private void checkTeamMember(Long memberId, Long teamId){
        if(!teamRepository.existTeamMember(memberId, teamId)){
            throw new NoAuthorityException();
        }
    }

    private void checkScheduleMember(Long memberId, Long scheduleId, boolean isMember){
        if(scheduleRepository.existScheduleMember(memberId, scheduleId) != isMember){
            if(isMember){
                throw new NoAuthorityException();
            }else{
                throw new BaseExceptionImpl(BaseErrorCode.ALREADY_IN_TEAM);
            }
        }
    }

    private void checkScheduleUpdateTime(Schedule schedule){
        long minutesDiff = Duration.between(LocalDateTime.now(), schedule.getScheduleTime()).toMinutes();
        if(minutesDiff < 60){
            throw new BaseExceptionImpl(BaseErrorCode.FORBIDDEN_SCHEDULE_UPDATE_TIME);
        }
    }
}
