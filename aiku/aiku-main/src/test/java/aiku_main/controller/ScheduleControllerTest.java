package aiku_main.controller;

import aiku_main.dto.LocationDto;
import aiku_main.dto.ScheduleAddDto;
import aiku_main.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    ScheduleService scheduleService;

    @InjectMocks
    ScheduleController scheduleController;

    @Test
    void addSchedule() {
        //when
        ScheduleAddDto dto = new ScheduleAddDto(" ",
                new LocationDto("loc1", 1.1, 1.1), LocalDateTime.now(), 0);
        scheduleController.addSchedule(1L, dto);
    }

    @Test
    void updateSchedule() {
    }
}