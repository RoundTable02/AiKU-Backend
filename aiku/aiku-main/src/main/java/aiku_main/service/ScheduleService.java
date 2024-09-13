package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import common.domain.Member;
import common.domain.Schedule;
import common.domain.Status;
import common.exception.NoAuthorityException;
import common.exception.NotEnoughPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.NoSuchElementException;

import static aiku_main.application_event.event.PointChangeReason.SCHEDULE;
import static aiku_main.application_event.event.PointChangeType.MINUS;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PointChangeEventPublisher pointChangeEventPublisher;

    //TODO 알림 추가하는 카프카 로직 추가해야됨
    @Transactional
    public Long addSchedule(Member member, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        //서비스 로직
        Schedule schedule = Schedule.create(member, teamId,
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation(),
                scheduleDto.getPointAmount());
        scheduleRepository.save(schedule);

        pointChangeEventPublisher.publish(member.getId(), MINUS, scheduleDto.getPointAmount(), SCHEDULE, schedule.getId());

        return schedule.getId();
    }

    //TODO 시간 바꿨을때 알림등 바꾸는 로직 추가해야됨
    @Transactional
    public Long updateSchedule(Member member, Long scheduleId, ScheduleUpdateDto scheduleDto){
        //검증 로직
        checkIsOwner(member.getId(), scheduleId);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        checkIsAlive(schedule);

        //서비스 로직
        schedule.update(scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.location);

        return schedule.getId();
    }

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

}
