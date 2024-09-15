package aiku_main.service;

import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleEnterDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import aiku_main.repository.TeamRepository;
import aiku_main.scheduler.ScheduleScheduler;
import common.domain.member.Member;
import common.domain.Schedule;
import common.domain.Status;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static aiku_main.application_event.event.PointChangeReason.SCHEDULE;
import static aiku_main.application_event.event.PointChangeType.MINUS;
import static aiku_main.application_event.event.PointChangeType.PLUS;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TeamRepository teamRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;
    private final ScheduleEventPublisher scheduleEventPublisher;
    private final ScheduleScheduler scheduleScheduler;

    //TODO 카프카를 통한 푸시 알림 로직 추가해야됨
    @Transactional
    public Long addSchedule(Member member, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        checkTeamMember(member.getId(), teamId);
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        //서비스 로직
        Schedule schedule = Schedule.create(member, teamId,
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation(),
                scheduleDto.getPointAmount());
        scheduleRepository.save(schedule);

        pointChangeEventPublisher.publish(member.getId(), MINUS, scheduleDto.getPointAmount(), SCHEDULE, schedule.getId());
        scheduleScheduler.reserveSchedule(schedule.getId(), schedule.getScheduleTime());

        return schedule.getId();
    }

    //TODO 카프카를 통한 푸시 알림 로직 추가해야됨
    @Transactional
    public Long updateSchedule(Member member, Long scheduleId, ScheduleUpdateDto scheduleDto){
        //검증 로직
        checkIsOwner(member.getId(), scheduleId);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);

        //서비스 로직
        schedule.update(scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.location);

        scheduleScheduler.changeSchedule(schedule.getId(), schedule.getScheduleTime());

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

        //서비스 로직
        schedule.addScheduleMember(member, false, enterDto.getPointAmount());

        if(enterDto.getPointAmount() > 0) {
            pointChangeEventPublisher.publish(member.getId(), MINUS, enterDto.getPointAmount(), SCHEDULE, scheduleId)
        };

        return schedule.getId();
    }

    @Transactional
    public Long exitSchedule(Member member, Long teamId, Long scheduleId) {
        //검증 로직
        checkScheduleMember(member.getId(), scheduleId, true);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);

        //서비스 로직
        int schedulePoint = schedule.removeScheduleMember(member);
        if(schedulePoint > 0){
            pointChangeEventPublisher.publish(member.getId(), PLUS, schedulePoint, SCHEDULE, schedule.getId());
        }

        scheduleEventPublisher.publish(member.getId(), scheduleId);

        return schedule.getId();
    }

    //== 이벤트 핸들러 실행 메서드 ==
    @Transactional
    public void exitAllScheduleInTeam(Long memberId, Long teamId) {
        
    }

    //== 편의 메서드 ==
    private void checkIsAlive(Schedule schedule){
        if(schedule.getStatus() == Status.DELETE){
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
                throw new BaseExceptionImpl(BaseErrorCode.AlreadyInTeam);
            }
        }
    }
}
