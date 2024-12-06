package aiku_main.integration_test;

import aiku_main.dto.team.*;
import aiku_main.dto.team.TeamResDto;
import aiku_main.exception.TeamException;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.TeamQueryRepository;
import aiku_main.service.TeamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.*;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import common.domain.value_reference.ScheduleMemberValue;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class TeamServiceIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    TeamService teamService;
    @Autowired
    TeamQueryRepository teamQueryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;

    Member member1;
    Member member2;
    Member member3;

    @BeforeEach
    void beforeEach(){
        member1 = Member.create("member1");
        member2 = Member.create("member2");
        member3 = Member.create("member3");
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
    }

    @Test
    void 그룹_등록() {
        //when
        TeamAddDto teamDto = new TeamAddDto("group1");
        Long teamId = teamService.addTeam(member1.getId(), teamDto);

        //then
        Team team = teamQueryRepository.findById(teamId).orElse(null);
        assertThat(team).isNotNull();
        assertThat(team.getTeamName()).isEqualTo(teamDto.getGroupName());
        assertThat(team.getTeamMembers()).hasSize(1);

        TeamMember teamMember = team.getTeamMembers().get(0);
        assertThat(teamMember.getMember().getId()).isEqualTo(member1.getId()); //그룹을 등록한 멤버는 그룹 자동 참가됨
    }

    @Test
    void 그룹_입장() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        Long teamId = teamService.enterTeam(member2.getId(), team.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(teamId).orElse(null);
        assertThat(resultTeam).isNotNull();
        assertThat(resultTeam.getTeamMembers()).hasSize(2);
        assertThat(resultTeam.getTeamMembers())
                .extracting(teamMember -> teamMember.getMember().getId())
                .containsExactlyInAnyOrder(member1.getId(), member2.getId());
    }

    @Test
    void 그룹_입장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        //when
        assertThatThrownBy(() -> teamService.enterTeam(member2.getId(), team.getId()))
                .isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_퇴장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        em.persist(team);

        //when
        teamService.exitTeam(member2.getId(), team.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();
        assertThat(resultTeam.getStatus()).isEqualTo(Status.ALIVE);

        TeamMember teamMember = teamQueryRepository.findDeletedTeamMember(team.getId(), member2.getId()).orElse(null);
        assertThat(teamMember).isNotNull();
    }

    @Test
    void 그룹_퇴장_그룹멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        assertThatThrownBy(() -> teamService.exitTeam(member2.getId(), team.getId()))
                .isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_퇴장_실행중인스케줄O() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        Schedule schedule = Schedule.create(member1, new TeamValue(team), "sche1",
                LocalDateTime.now().plusDays(1), new Location("loc", 1.0, 1.0), 0);
        schedule.setRun();
        em.persist(schedule);

        //when
        assertThatThrownBy(() -> teamService.exitTeam(member1.getId(), team.getId()))
                .isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_퇴장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        em.persist(team);

        teamService.exitTeam(member2.getId(), team.getId());

        //when
        assertThatThrownBy(() -> teamService.exitTeam(member2.getId(), team.getId()))
                .isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_퇴장_남은멤버x_그룹삭제() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        teamService.exitTeam(member1.getId(), team.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();
        assertThat(resultTeam.getStatus()).isEqualTo(Status.DELETE);
    }

    @Test
    void 그룹_상세_조회() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        //when
        TeamDetailResDto result = teamService.getTeamDetail(member1.getId(), team.getId());

        //then
        assertThat(result.getGroupId()).isEqualTo(team.getId());
        assertThat(result.getGroupName()).isEqualTo(team.getTeamName());

        List<TeamMemberResDto> teamMemberDtos = result.getMembers();
        assertThat(teamMemberDtos).hasSize(2);
        assertThat(teamMemberDtos)
                .extracting(TeamMemberResDto::getMemberId)
                .containsExactly(member1.getId(), member2.getId());
    }

    @Test
    void 그룹_상세_조회_그룹멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        //when
        assertThatThrownBy(() -> teamService.getTeamDetail(member2.getId(), team.getId()))
                .isInstanceOf(TeamException.class);
    }

    @Test
    void 그룹_목록_조회() {
        //given
        Team teamA = Team.create(member1, "teamA");
        em.persist(teamA);

        Team teamB = Team.create(member1, "teamB");
        teamB.addTeamMember(member2);
        em.persist(teamB);

        Team teamC = Team.create(member1, "teamC");
        teamC.addTeamMember(member2);
        teamC.addTeamMember(member3);
        em.persist(teamC);

        Schedule scheduleA1 = createSchedule(member1, teamA, LocalDateTime.now().minusDays(1));
        scheduleA1.setTerm(LocalDateTime.now());
        em.persist(scheduleA1);

        Schedule scheduleB1 = createSchedule(member1, teamA, LocalDateTime.now().minusDays(2));
        scheduleB1.setTerm(LocalDateTime.now());
        em.persist(scheduleB1);

        Schedule scheduleB2 = createSchedule(member1, teamA, LocalDateTime.now().plusDays(1));
        em.persist(scheduleB2);

        Schedule scheduleC1 = createSchedule(member1, teamA, LocalDateTime.now());
        scheduleC1.setTerm(LocalDateTime.now());
        em.persist(scheduleC1);

        //when
        List<TeamResDto> data = teamService.getTeamList(member1.getId(), 1).getData();

        //then
        assertThat(data).hasSize(3);
        assertThat(data)
                .extracting(TeamResDto::getGroupId)
                .containsExactly(teamC.getId(), teamA.getId(), teamB.getId());
        assertThat(data)
                .extracting(TeamResDto::getMemberSize)
                .containsExactly(3, 1, 2);
    }

    @Test
    void 이벤트핸들러_그룹_지각_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        LocalDateTime scheduleTime = LocalDateTime.now().minusDays(1);

        Schedule schedule1 = createSchedule(member1, team, scheduleTime);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        // member1: 10, member2: 0, member3: 15분 지각
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), scheduleTime.plusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), scheduleTime.minusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), scheduleTime.plusMinutes(15));
        schedule1.setTerm(schedule1.getScheduleTime().plusMinutes(30));

        Schedule schedule2 = createSchedule(member1, team, scheduleTime);
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        // member1: 20, member2: 0, member3: 0분 지각
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(0), scheduleTime.plusMinutes(20));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(1), scheduleTime.minusMinutes(10));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(2), scheduleTime.minusMinutes(30));
        schedule2.setTerm(schedule2.getScheduleTime().plusMinutes(30));

        //when
        teamService.analyzeLateTimeResult(schedule1.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();

        //누적 member1: 30, member2: 0, member3: 15분 지각
        List<TeamMemberResult> lateMemberRanking = objectMapper.readValue(resultTeam.getTeamResult().getLateTimeResult(), TeamLateTimeResult.class).getMembers();
        assertThat(lateMemberRanking)
                .extracting(TeamMemberResult::getMemberId)
                .containsExactly(member1.getId(), member3.getId());
        assertThat(lateMemberRanking)
                .extracting(TeamMemberResult::getAnalysis)
                .containsExactly(-30, -15);
    }

    @Test
    void 이벤트핸들러_그룹_지각_분석_이전결과존재() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        LocalDateTime scheduleTime = LocalDateTime.now().minusDays(1);

        Schedule schedule1 = createSchedule(member1, team, scheduleTime);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        //member1: 20, member2: 10, member3: 5분 지각
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), scheduleTime.plusMinutes(20));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), scheduleTime.plusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), scheduleTime.plusMinutes(5));
        schedule1.setTerm(schedule1.getScheduleTime().plusMinutes(30));

        teamService.analyzeLateTimeResult(schedule1.getId());

        Schedule schedule2 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        //member1: 10, member2: 50, member3: 100분 지각
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(0), scheduleTime.plusMinutes(10));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(1), scheduleTime.plusMinutes(50));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(2), scheduleTime.plusMinutes(100));
        schedule2.setTerm(schedule2.getScheduleTime().plusMinutes(30));

        //when
        //member1: 30, member2: 60, member3: 105분 지각
        teamService.analyzeLateTimeResult(schedule1.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();

        List<TeamMemberResult> lateMemberRanking = objectMapper.readValue(resultTeam.getTeamResult().getLateTimeResult(), TeamLateTimeResult.class).getMembers();
        assertThat(lateMemberRanking)
                .extracting(TeamMemberResult::getPreviousRank)
                .containsExactly(3, 2, 1);
        assertThat(lateMemberRanking)
                .extracting(TeamMemberResult::getRank)
                .containsExactly(1, 2, 3);
    }

    @Test
    void 이벤트핸들러_베팅_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        //member1==member2 >> 100아쿠
        Betting betting1 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), 100);
        betting1.setDraw();
        em.persist(betting1);

        //member2>member1 >> 100아쿠
        Betting betting2 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting2.setWin(100);
        em.persist(betting2);

        //member3>member1 >> 100아쿠
        Betting betting3 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(2)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting3.setWin(100);
        em.persist(betting3);

        //member2==member3 >> 100아쿠
        Betting betting4 = Betting.create(new ScheduleMemberValue(schedule2.getScheduleMembers().get(1)), new ScheduleMemberValue(schedule2.getScheduleMembers().get(2)), 100);
        betting4.setDraw();
        em.persist(betting4);

        //member3>member1 >> 100아쿠
        Betting betting5 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(2)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting5.setWin(100);
        em.persist(betting5);

        //when
        teamService.analyzeBettingResult(schedule1.getId());

        //then
        //member1:0 member2:100 member3:200
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();

        List<TeamMemberResult> teamMemberResults = objectMapper.readValue(resultTeam.getTeamResult().getTeamBettingResult(), TeamBettingResult.class).getMembers();
        assertThat(teamMemberResults).hasSize(3);
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getMemberId)
                .containsExactly(member3.getId(), member2.getId(), member1.getId());
    }

    @Test
    void 이벤트핸들러_베팅_분석_이전결과존재() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        //member1>member2 >> 100아쿠
        Betting betting1 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), 100);
        betting1.setWin(100);
        em.persist(betting1);

        //member2<member3 >> 100아쿠
        Betting betting2 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(2)), 100);
        betting2.setLose();
        em.persist(betting2);

        teamService.analyzeBettingResult(schedule1.getId());

        //member1>member2 >> 100아쿠
        Betting betting4 = Betting.create(new ScheduleMemberValue(schedule2.getScheduleMembers().get(0)), new ScheduleMemberValue(schedule2.getScheduleMembers().get(1)), 100);
        betting4.setWin(100);
        em.persist(betting4);

        //member3>member2 >> 100아쿠
        Betting betting5 = Betting.create(new ScheduleMemberValue(schedule2.getScheduleMembers().get(2)), new ScheduleMemberValue(schedule2.getScheduleMembers().get(1)), 100);
        betting5.setWin(100);
        em.persist(betting5);

        //when
        teamService.analyzeBettingResult(schedule1.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();

        List<TeamMemberResult> teamMemberResults = objectMapper.readValue(resultTeam.getTeamResult().getTeamBettingResult(), TeamBettingResult.class).getMembers();
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getRank)
                .containsExactly(1, 2, 3);
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getPreviousRank)
                .containsExactly(1, -1, 2);
    }

    @Test
    void 이벤트핸들러_레이싱_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        //member1 > member2 >> 20아쿠
        Racing racing1 = Racing.create(schedule1.getScheduleMembers().get(0).getId(), schedule1.getScheduleMembers().get(1).getId(), 20);
        racing1.termRacing(schedule1.getScheduleMembers().get(0).getId());
        em.persist(racing1);

        //member2 > member3 >> 10아쿠
        Racing racing2 = Racing.create(schedule1.getScheduleMembers().get(1).getId(), schedule1.getScheduleMembers().get(2).getId(), 10);
        racing2.termRacing(schedule1.getScheduleMembers().get(1).getId());
        em.persist(racing2);

        //member1 < member2 >> 20아쿠
        Racing racing3 = Racing.create(schedule2.getScheduleMembers().get(0).getId(), schedule2.getScheduleMembers().get(1).getId(), 20);
        racing3.termRacing(schedule2.getScheduleMembers().get(0).getId());
        em.persist(racing3);

        //member2 > member3 >> 10아쿠
        Racing racing4 = Racing.create(schedule2.getScheduleMembers().get(1).getId(), schedule2.getScheduleMembers().get(2).getId(), 10);
        racing4.termRacing(schedule2.getScheduleMembers().get(1).getId());
        em.persist(racing4);

        //member1 > member3 >> 10아쿠
        Racing racing5 = Racing.create(schedule2.getScheduleMembers().get(2).getId(), schedule2.getScheduleMembers().get(0).getId(), 10);
        racing5.termRacing(schedule2.getScheduleMembers().get(0).getId());
        em.persist(racing5);

        //when
        teamService.analyzeRacingResult(schedule1.getId());

        //then
        //member1: 30, member2: 20, member3: 0
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();

        List<TeamMemberResult> teamMemberResults = objectMapper.readValue(resultTeam.getTeamResult().getTeamRacingResult(), TeamRacingResult.class).getMembers();
        assertThat(teamMemberResults).hasSize(3);
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getMemberId)
                .containsExactly(member1.getId(), member2.getId(), member3.getId());
    }

    @Test
    void 이벤트핸들러_레이싱_분석_이전결과존재() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = createSchedule(member1, team, LocalDateTime.now().minusDays(1));
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        Racing racing1 = Racing.create(schedule1.getScheduleMembers().get(0).getId(), schedule1.getScheduleMembers().get(1).getId(), 20);
        racing1.termRacing(schedule1.getScheduleMembers().get(0).getId());
        em.persist(racing1);

        Racing racing2 = Racing.create(schedule1.getScheduleMembers().get(1).getId(), schedule1.getScheduleMembers().get(2).getId(), 10);
        racing2.termRacing(schedule1.getScheduleMembers().get(1).getId());
        em.persist(racing2);

        teamService.analyzeRacingResult(schedule1.getId());

        Member member4 = Member.create("member4");
        em.persist(member4);
        team.addTeamMember(member4);
        schedule2.addScheduleMember(member4, false, 0);

        em.flush();
        em.clear();

        Racing racing3 = Racing.create(schedule2.getScheduleMembers().get(0).getId(), schedule2.getScheduleMembers().get(1).getId(), 20);
        racing3.termRacing(schedule2.getScheduleMembers().get(0).getId());
        em.persist(racing3);

        Racing racing4 = Racing.create(schedule2.getScheduleMembers().get(1).getId(), schedule2.getScheduleMembers().get(2).getId(), 10);
        racing4.termRacing(schedule2.getScheduleMembers().get(1).getId());
        em.persist(racing4);

        Racing racing5 = Racing.create(schedule2.getScheduleMembers().get(2).getId(), schedule2.getScheduleMembers().get(3).getId(), 10);
        racing5.termRacing(schedule2.getScheduleMembers().get(2).getId());
        em.persist(racing5);

        //when
        teamService.analyzeRacingResult(schedule1.getId());

        //then
        Team resultTeam = teamQueryRepository.findById(team.getId()).orElse(null);
        assertThat(resultTeam).isNotNull();
        System.out.println(resultTeam.getTeamResult().getTeamRacingResult());

        List<TeamMemberResult> teamMemberResults = objectMapper.readValue(resultTeam.getTeamResult().getTeamRacingResult(), TeamRacingResult.class).getMembers();
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getRank)
                .containsExactly(1, 2, 3, 4);
        assertThat(teamMemberResults)
                .extracting(TeamMemberResult::getPreviousRank)
                .containsExactly(1, 2, 3, -1);
    }

    Schedule createSchedule(Member member, Team team, LocalDateTime startTime) {
        return Schedule.create(member, new TeamValue(team), "schedule", startTime,
                new Location("loc1", 1.1, 1.1), 0);
    }
}
