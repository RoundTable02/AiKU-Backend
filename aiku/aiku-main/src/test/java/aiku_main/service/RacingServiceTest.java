package aiku_main.service;

import aiku_main.dto.racing.ScheduleRacing;
import aiku_main.dto.racing.ScheduleRacingMember;
import aiku_main.dto.racing.ScheduleRacingResult;
import aiku_main.repository.RacingQueryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.Location;
import common.domain.Racing;
import common.domain.member.Member;
import common.domain.member.MemberProfileBackground;
import common.domain.member.MemberProfileCharacter;
import common.domain.member.MemberProfileType;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class RacingServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    RacingService racingService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ObjectMapper objectMapper;

    Member member1;
    Member member2;
    Member member3;

    ScheduleMember scheduleMember1;
    ScheduleMember scheduleMember2;
    ScheduleMember scheduleMember3;

    Team team1;
    Schedule schedule1;

    Racing racing1;
    Racing racing2;

    @Autowired
    private RacingQueryRepository racingRepository;

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

        member1.updateProfile(MemberProfileType.CHAR, null, MemberProfileCharacter.C01, MemberProfileBackground.GRAY);
        member2.updateProfile(MemberProfileType.CHAR, null, MemberProfileCharacter.C01, MemberProfileBackground.GRAY);
        member3.updateProfile(MemberProfileType.CHAR, null, MemberProfileCharacter.C01, MemberProfileBackground.GRAY);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        team1 = Team.create(member1, "team1");

        em.persist(team1);

        schedule1 = Schedule.create(member1, new TeamValue(team1.getId()), "schedule1",
                LocalDateTime.of(2100, Month.JANUARY, 11, 13, 30, 00),
                new Location("location1", 1.0, 1.0), 30);

        em.persist(schedule1);

        schedule1.addScheduleMember(member2, false, 30);
        schedule1.addScheduleMember(member3, false, 30);

        em.flush();

        List<ScheduleMember> scheduleMembers = schedule1.getScheduleMembers();

        for (ScheduleMember scheduleMember : scheduleMembers) {
            if (scheduleMember.getMember().getId().equals(member1.getId())) {
                scheduleMember1 = scheduleMember;
            } else if (scheduleMember.getMember().getId().equals(member2.getId())) {
                scheduleMember2 = scheduleMember;
            } else {
                scheduleMember3 = scheduleMember;
            }
        }
        schedule1.setRun();

        System.out.println("========= init done =========");
    }

    @AfterEach
    void afterEach() {
        racingRepository.deleteAll();
    }

    @Test
    void 레이싱_결과_분석() {
        racing1 = Racing.create(scheduleMember1.getId(), scheduleMember2.getId(), 20);
        racing1.termRacing(scheduleMember1.getId());
        em.persist(racing1);

        racing2 = Racing.create(scheduleMember2.getId(), scheduleMember3.getId(), 10);
        racing2.termRacing(scheduleMember2.getId());
        em.persist(racing2);

        em.flush();
        em.clear();

        schedule1 = em.find(Schedule.class, schedule1.getId());
        schedule1.close(LocalDateTime.now());

        em.flush();
        em.clear();

        racingService.analyzeScheduleRacingResult(schedule1.getId());

        em.flush();
        em.clear();

        String scheduleRacingResult = scheduleService.getScheduleRacingResult(member1.getId(), team1.getId(), schedule1.getId());

        ScheduleRacingResult scheduleRacingResultObj = null;
        try {
            scheduleRacingResultObj = objectMapper.readValue(scheduleRacingResult, ScheduleRacingResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<ScheduleRacing> data = scheduleRacingResultObj.getData();
        assertThat(data).extracting(ScheduleRacing::getFirstRacer).extracting(ScheduleRacingMember::getMemberId).containsExactly(member1.getId(), member2.getId());
        assertThat(data.get(0).getWinnerId()).isEqualTo(scheduleMember1.getId());
    }
}
