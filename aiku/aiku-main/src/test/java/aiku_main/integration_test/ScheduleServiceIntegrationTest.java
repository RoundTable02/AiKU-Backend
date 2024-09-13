package aiku_main.integration_test;

import aiku_main.dto.ScheduleUpdateDto;
import aiku_main.repository.ScheduleRepository;
import aiku_main.service.ScheduleService;
import common.domain.Location;
import common.domain.Member;
import common.domain.Schedule;
import common.domain.Team;
import common.exception.NoAuthorityException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class ScheduleServiceIntegrationTest {

    @Autowired
    EntityManager em;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleRepository scheduleRepository;

    Random random = new Random();

    @Test
    @DisplayName("스케줄 수정(권한O&X)")
    void updateSchedule() {
        //given
        Member member = Member.create("member1");
        Member member2 = Member.create("member2");
        em.persist(member);
        em.persist(member2);

        Team team = Team.create(member, "team1");
        em.persist(team);

        Schedule schedule = createSchedule(member, team.getId(), 0);
        em.persist(schedule);

        //when
        ScheduleUpdateDto scheduleDto = new ScheduleUpdateDto("new",
                new Location("new", 2.0, 2.0), LocalDateTime.now().plusHours(2));
        scheduleService.updateSchedule(member, schedule.getId(), scheduleDto);

        //then
        Schedule findSchedule = scheduleRepository.findById(schedule.getId()).get();
        assertThat(findSchedule.getScheduleName()).isEqualTo(scheduleDto.getScheduleName());
        assertThat(findSchedule.getLocation().getLocationName()).isEqualTo(scheduleDto.getLocation().getLocationName());

        //권한 x
        assertThatThrownBy(() -> scheduleService.updateSchedule(member2, schedule.getId(), scheduleDto)).isInstanceOf(NoAuthorityException.class);
    }

    Schedule createSchedule(Member member, Long teamId, int pointAmount){
        return Schedule.create(member, teamId, UUID.randomUUID().toString(), LocalDateTime.now(),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()), pointAmount);
    }
}
