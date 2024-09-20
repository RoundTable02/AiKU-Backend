package aiku_main.service;

import aiku_main.application_event.publisher.ScheduleEventPublisher;
import aiku_main.dto.LocationDto;
import aiku_main.dto.ScheduleDetailResDto;
import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleReadRepository;
import aiku_main.repository.ScheduleRepository;
import aiku_main.repository.TeamRepository;
import aiku_main.scheduler.ScheduleScheduler;
import common.domain.Location;
import common.domain.member.Member;
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
    @Mock
    ScheduleReadRepository scheduleReadRepository;
    @Mock
    TeamRepository teamRepository;
    @Mock
    ScheduleScheduler scheduleScheduler;
    @Mock
    ScheduleEventPublisher scheduleEventPublisher;

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
                new LocationDto("new location", 2.2, 2.2),
                LocalDateTime.now());
        Long resultId = scheduleService.updateSchedule(member, schedule.getId(), scheduleDto);

        //then
        assertThat(resultId).isEqualTo(scheduleId);
        assertThat(schedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
    }

    @Test
    void getScheduleDetail(){
        //given
        Member member1 = createSpyMember();
        Member member2 = createSpyMember();

        Schedule schedule = createSpySchedule(member1, null, 0);
        schedule.addScheduleMember(member2, false, 0);


        when(scheduleRepository.existScheduleMember(any(), any())).thenReturn(true);
        when(scheduleRepository.findById(nullable(Long.class))).thenReturn(Optional.of(schedule));
        when(scheduleReadRepository.getScheduleMembersWithMember(nullable(Long.class))).thenReturn(null);

        //when
        ScheduleDetailResDto result = scheduleService.getScheduleDetail(member1, null, null);

        //then
        assertThat(result.getScheduleName()).isEqualTo(schedule.getScheduleName());
        assertThat(result.getLocation().getLocationName()).isEqualTo(schedule.getLocation().getLocationName());
    }

    Member createSpyMember(){
        return spy(new Member(randomUUID().toString()));
    }

    Schedule createSpySchedule(Member member, Long teamId, int pointAmount){
        return spy(Schedule.create(member, null,
                randomUUID().toString(), LocalDateTime.now().plusHours(3),
                new Location(randomUUID().toString(), random(), random()), pointAmount));
    }

    Long getRandomId(){
        Random random = new Random();
        return random.nextLong();
    }
}