package aiku_main.service.schedule;

import aiku_main.dto.schedule.ScheduleArrivalMember;
import aiku_main.dto.schedule.ScheduleArrivalResult;
import aiku_main.dto.schedule.result.betting.BettingResult;
import aiku_main.dto.schedule.result.betting.BettingResultDto;
import aiku_main.dto.schedule.result.racing.RacingResult;
import aiku_main.dto.schedule.result.racing.RacingResultDto;
import aiku_main.repository.schedule.ScheduleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.domain.Location;
import common.domain.betting.Betting;
import common.domain.member.Member;
import common.domain.racing.Racing;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.value_reference.ScheduleMemberValue;
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
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        team.addTeamMember(member4);
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

    /**
     * //given
     * member1 -> member2
     * member3 -> member4
     * 베팅 2개 생성
     *
     * //then
     * member1,3이 bettor
     * member2,4가 betee인지 검증
     */
    @Test
    void 베팅_결과_분석() {
        //given
        Schedule schedule = createSchedule(member1, team);
        schedule.addScheduleMember(member2, false, 10);
        schedule.addScheduleMember(member3, false, 10);
        schedule.addScheduleMember(member4, false, 10);

        scheduleRepository.save(schedule);

        Betting betting1 = createBetting(
                schedule.getScheduleMembers().get(0).getId(),
                schedule.getScheduleMembers().get(1).getId()
        );
        Betting betting2 = createBetting(
                schedule.getScheduleMembers().get(2).getId(),
                schedule.getScheduleMembers().get(3).getId()
        );

        betting1.setDraw();
        betting2.setLose();

        em.persist(betting1);
        em.persist(betting2);

        //when
        scheduleResultAnalysisService.analyzeBettingResult(schedule.getId());

        //then
        String BettingResultStr = schedule.getScheduleResult().getScheduleBettingResult();
        List<BettingResult> data = ObjectMapperUtil
                .parseJson(BettingResultStr, BettingResultDto.class)
                .getData();

        assertThat(data).hasSize(2);

        assertThat(data)
                .extracting(result -> result.getBettor().getMemberId())
                .contains(member1.getId(), member3.getId());

        assertThat(data)
                .extracting(result -> result.getBetee().getMemberId())
                .contains(member2.getId(), member4.getId());
    }

    /**
     * //given
     * member1, member2 -> 1win
     * member2, member4 -> 2win
     * member3, member4 -> 4win
     */
    @Test
    void 레이싱_결과_분석() {
        //given
        Schedule schedule = createSchedule(member1, team);
        schedule.addScheduleMember(member2, false, 10);
        schedule.addScheduleMember(member3, false, 10);
        schedule.addScheduleMember(member4, false, 10);

        scheduleRepository.save(schedule);

        int point1 = 100;
        Racing racing1 = createRacing(
                schedule.getScheduleMembers().get(0).getId(),
                schedule.getScheduleMembers().get(1).getId(),
                point1
        );
        racing1.termRacing(member1.getId());

        int point2 = 50;
        Racing racing2 = createRacing(
                schedule.getScheduleMembers().get(1).getId(),
                schedule.getScheduleMembers().get(3).getId(),
                point2
        );
        racing2.termRacing(member2.getId());

        int point3 = 10;
        Racing racing3 = createRacing(
                schedule.getScheduleMembers().get(2).getId(),
                schedule.getScheduleMembers().get(3).getId(),
                point3
        );
        racing3.termRacing(member4.getId());

        em.persist(racing1);
        em.persist(racing2);
        em.persist(racing3);

        //when
        scheduleResultAnalysisService.analyzeRacingResult(schedule.getId());

        String racingResultStr = schedule.getScheduleResult().getScheduleRacingResult();
        List<RacingResult> racingResults = ObjectMapperUtil
                .parseJson(racingResultStr, RacingResultDto.class)
                .getData();

        assertThat(racingResults).hasSize(3);

        assertThat(racingResults)
                .extracting(result -> result.getFirstRacer().getMemberId())
                .containsExactlyInAnyOrder(member1.getId(), member2.getId(), member3.getId());

        assertThat(racingResults)
                .extracting(result -> result.getSecondRacer().getMemberId())
                .containsExactlyInAnyOrder(member2.getId(), member4.getId(), member4.getId());

        assertThat(racingResults)
                .extracting(result -> result.getWinnerId())
                .containsExactlyInAnyOrder(member1.getId(), member2.getId(), member4.getId());

        assertThat(racingResults)
                .extracting(result -> result.getPointAmount())
                .containsExactlyInAnyOrder(point1, point2, point3);
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

    Betting createBetting(Long bettorScheMembId, Long betteScheMemId){
        return Betting.create(
                new ScheduleMemberValue(bettorScheMembId),
                new ScheduleMemberValue(betteScheMemId),
                100
        );
    }

    Racing createRacing(Long firstScheMembId, Long secondScheMemId, int point){
        return Racing.create(
                firstScheMembId,
                secondScheMemId,
                point
        );
    }
}