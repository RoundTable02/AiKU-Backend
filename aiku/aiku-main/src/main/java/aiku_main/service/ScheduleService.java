package aiku_main.service;

import aiku_main.dto.ScheduleAddDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import common.domain.Member;
import common.domain.Schedule;
import common.domain.Status;
import common.exception.NoAuthorityException;
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

    public Long addSchedule(ScheduleAddDto scheduleDto){

        return null;
    }

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
        if(schedule.getStatus() == Status.DELETE) throw new NoSuchElementException();
    }

    private void checkIsOwner(Long memberId, Long scheduleId){
        if(!scheduleRepository.isScheduleOwner(memberId, scheduleId)){
            throw new NoAuthorityException();
        }
    }

}
