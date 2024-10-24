package map.service;

import common.domain.Location;
import common.domain.Racing;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.EntityManager;
import map.dto.DataResDto;
import map.dto.RacingAddDto;
import map.dto.RacingResDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Transactional
@SpringBootTest(properties = "spring.config.location=classpath:application-test.yml")
class RacingServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private RacingService racingService;

    Member member1;
    Member member2;
    Member member3;

    Schedule schedule1;

    Racing racing1;
    Racing racing2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
                .kakaoId(1L)
                .nickname("member1")
                .email("member1@sample.com")
                .password("1")
                .build();

        member2 = Member.builder()
                .kakaoId(2L)
                .nickname("member2")
                .email("member2@sample.com")
                .password("2")
                .build();

        member3 = Member.builder()
                .kakaoId(3L)
                .nickname("member3")
                .email("member3@sample.com")
                .password("3")
                .build();

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        Team team1 = Team.create(member1, "team1");

        em.persist(team1);

        schedule1 = Schedule.create(member1, new TeamValue(team1), "schedule1",
                LocalDateTime.of(2100, Month.JANUARY, 11, 13, 30, 00),
                new Location("location1", 1.0, 1.0), 30);

        em.persist(schedule1);

        schedule1.addScheduleMember(member1, true, 30);
        schedule1.addScheduleMember(member2, false, 30);
        schedule1.addScheduleMember(member3, false, 30);

        em.flush();

        List<ScheduleMember> scheduleMembers = schedule1.getScheduleMembers();

        schedule1.setRun();

        racing1 = Racing.create(scheduleMembers.get(0).getId(), scheduleMembers.get(1).getId(), 20);
        em.persist(racing1);

        racing1.startRacing();

        racing2 = Racing.create(scheduleMembers.get(1).getId(), scheduleMembers.get(2).getId(), 10);
        em.persist(racing2);

        racing2.startRacing();

        em.flush();
        em.clear();

        System.out.println("========= init done =========");
    }

    @Test
    void 레이싱_리스트_조회() {
        DataResDto<List<RacingResDto>> racings = racingService.getRacings(member1.getId(), schedule1.getId());

        List<RacingResDto> data = racings.getData();

        Assertions.assertThat(data.size()).isEqualTo(2);
    }

    @Test
    void 레이싱_생성() {
        RacingAddDto racingAddDto = new RacingAddDto(member3.getId(), 0);
        Long racingId = racingService.makeRacing(member1.getId(), schedule1.getId(), racingAddDto);

        Racing findRacing = em.find(Racing.class, racingId);

        System.out.println(findRacing.getRaceStatus());
    }




}