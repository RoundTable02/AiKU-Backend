package aiku_main.service;

import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import common.domain.Location;
import common.domain.Member;
import common.domain.Schedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    ScheduleRepository scheduleRepository;

    @InjectMocks
    ScheduleService scheduleService;

    @Test
    void updateSchedule() {
        //given
        Member member = new Member("member1");
        Schedule schedule = Schedule.create(member, null,
                "original schedule name", LocalDateTime.now(),
                new Location("original location", 1.1, 1.1));

        when(scheduleRepository.findById(nullable(Long.class))).thenReturn(Optional.of(schedule));
        when(scheduleRepository.isScheduleOwner(nullable(Long.class), nullable(Long.class))).thenReturn(true);

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new Schedule",
                new Location("new location", 2.2, 2.2),
                LocalDateTime.now());
        scheduleService.updateSchedule(member, schedule.getId(), scheduleDto);

        //then
        assertThat(schedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
        assertThat(schedule.getLocation()).isEqualTo(scheduleDto.getLocation());
    }
}