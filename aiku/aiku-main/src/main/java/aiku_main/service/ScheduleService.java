package aiku_main.service;

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

import java.util.NoSuchElementException;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 스케줄 등록
     * 참가비
     * 약속 참가자로 등록
     * 부족하면 예외
     */
    @Transactional
    public Long addSchedule(Member member, Long teamId, ScheduleAddDto scheduleDto){
        //검증 로직
        checkEnoughPoint(member, scheduleDto.getPointAmount());

        //서비스 로직
        //TODO 멤버 포인트 지불

        Schedule schedule = Schedule.create(member, teamId,
                scheduleDto.getScheduleName(), scheduleDto.getScheduleTime(), scheduleDto.getLocation(),
                scheduleDto.getPointAmount());
        scheduleRepository.save(schedule);

        return schedule.getId();
    }

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
