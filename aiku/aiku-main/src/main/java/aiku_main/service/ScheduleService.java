package aiku_main.service;

import aiku_main.dto.ScheduleAddDto;
import aiku_main.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Long addSchedule(ScheduleAddDto scheduleDto){

        return null;
    }
}
