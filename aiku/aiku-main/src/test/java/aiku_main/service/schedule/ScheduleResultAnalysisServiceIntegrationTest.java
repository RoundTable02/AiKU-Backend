package aiku_main.service.schedule;

import aiku_main.dto.schedule.ScheduleArrivalMember;
import aiku_main.dto.schedule.ScheduleArrivalResult;
import aiku_main.repository.schedule.ScheduleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.domain.Location;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.value_reference.TeamValue;
import common.util.ObjectMapperUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ScheduleResultAnalysisServiceIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    ScheduleResultAnalysisService scheduleResultAnalysisService;
    @Autowired
    ScheduleRepository scheduleRepository;

    Member teamOwner;
    Member member1;
    Member member2;
    Member member3;
    Member member4;
    Team team;

    @Value("${schedule.fee.participation}")
    private int scheduleEnterPoint;

    Random random = new Random();

    @BeforeEach
    void beforeEach(){
        teamOwner = createMember();
        member1 = createMember();
        member2 = createMember();
        member3 = createMember();
        member4 = createMember();
        em.persist(teamOwner);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        team = Team.create(teamOwner, "team");
        em.persist(team);
    }

    /**
     * //given
     * 3시간 뒤 스케줄 생성
     * member1 -> 1시간 지각
     * member2 -> 10분 지각
     * member3 -> 몇초 지각
     *
     * //then
     * 가장 늦게 온 순으로
     * member1, 2, 3순으로 조회
     */
    @Test
    void 이벤트핸들러_스케줄_도착순서_분석() throws JsonProcessingException {
        //given
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.flush();

        Schedule schedule1 = createSchedule(member1, team);
        schedule1.addScheduleMember(member2, false, scheduleEnterPoint);
        schedule1.addScheduleMember(member3, false, scheduleEnterPoint);
        em.persist(schedule1);

        LocalDateTime arrivalTime = LocalDateTime.now();
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), arrivalTime.plusHours(4));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), arrivalTime.plusHours(3).plusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), arrivalTime.plusHours(3));
        schedule1.setTerm(LocalDateTime.now());

        //when
        scheduleResultAnalysisService.analyzeScheduleArrivalResult(schedule1.getId());

        //then
        Schedule testSchedule = scheduleRepository.findById(schedule1.getId()).get();
        String scheduleArrivalResultStr = testSchedule.getScheduleResult().getScheduleArrivalResult();
        List<ScheduleArrivalMember> data = ObjectMapperUtil
                .parseJson(scheduleArrivalResultStr, ScheduleArrivalResult.class)
                .getMembers();

        //지각 많이한 순
        assertThat(data)
                .extracting(ScheduleArrivalMember::getMemberId)
                .containsExactly(member3.getId(), member2.getId(), member1.getId());
    }

    Member createMember(){
        Member member = Member.builder()
                .nickname(UUID.randomUUID().toString())
                .build();
        member.updatePointAmount(100);

        return member;
    }

    Schedule createSchedule(Member member, Team team){
        return Schedule.create(
                member,
                new TeamValue(team.getId()),
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(3),
                new Location(UUID.randomUUID().toString(), random.nextDouble(), random.nextDouble()),
                scheduleEnterPoint);
    }
}