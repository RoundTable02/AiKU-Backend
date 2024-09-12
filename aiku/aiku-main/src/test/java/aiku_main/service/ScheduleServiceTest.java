package aiku_main.service;

import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import common.domain.Location;
import common.domain.Member;
import common.domain.Schedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.random;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
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
        Member member = createSpyMember();
        Schedule schedule = createSpySchedule(member, null, 0);
        Long scheduleId = getRandomId();

        doReturn(scheduleId).when(schedule).getId();
        when(scheduleRepository.findById(nullable(Long.class))).thenReturn(Optional.of(schedule));
        when(scheduleRepository.isScheduleOwner(nullable(Long.class), nullable(Long.class))).thenReturn(true);

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new Schedule",
                new Location("new location", 2.2, 2.2),
                LocalDateTime.now());
        Long resultId = scheduleService.updateSchedule(member, schedule.getId(), scheduleDto);

        //then
        assertThat(resultId).isEqualTo(scheduleId);
        assertThat(schedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
        assertThat(schedule.getLocation()).isEqualTo(scheduleDto.getLocation());
    }

    Member createSpyMember(){
        return spy(new Member(randomUUID().toString()));
    }

    Schedule createSpySchedule(Member member, Long teamId, int pointAmount){
        return spy(Schedule.create(member, null,
                randomUUID().toString(), LocalDateTime.now(),
                new Location(randomUUID().toString(), random(), random()), pointAmount));
    }

    Long getRandomId(){
        Random random = new Random();
        return random.nextLong();
    }
}