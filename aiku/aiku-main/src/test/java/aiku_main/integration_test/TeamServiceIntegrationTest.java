package aiku_main.integration_test;

import aiku_main.application_event.domain.TeamBettingResult;
import aiku_main.application_event.domain.TeamLateTimeResult;
import aiku_main.application_event.domain.TeamResultMember;
import aiku_main.dto.*;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.TeamRepository;
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
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
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
    TeamRepository teamRepository;
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
        //given
        //when
        TeamAddDto teamDto = new TeamAddDto("group1");
        Long teamId = teamService.addTeam(member1, teamDto);

        em.flush();
        em.clear();

        //then
        Team team = teamRepository.findById(teamId).get();
        assertThat(team.getTeamName()).isEqualTo(teamDto.getGroupName());
        assertThat(team.getTeamMembers().size()).isEqualTo(1);

        TeamMember teamMember = team.getTeamMembers().get(0);
        assertThat(teamMember.getMember().getId()).isEqualTo(member1.getId());
    }

    @Test
    void 그룹_입장() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        em.flush();
        em.clear();

        //when
        Long teamId = teamService.enterTeam(member2, team.getId());

        //then
        TeamMember teamMember = teamRepository.findAliveTeamMember(teamId, member2.getId()).orElse(null);
        assertThat(teamMember).isNotNull();

        //중복 입장
        assertThatThrownBy(() -> teamService.enterTeam(member2, team.getId())).isInstanceOf(BaseExceptionImpl.class);
    }

    @Test
    void 그룹_입장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> teamService.enterTeam(member2, team.getId())).isInstanceOf(BaseExceptionImpl.class);
    }

    @Test
    void 그룹_퇴장() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        em.persist(team);

        em.flush();
        em.clear();

        //when
        teamService.exitTeam(member2, team.getId());
        em.flush();
        em.clear();

        //then
        Team findTeam = teamRepository.findById(team.getId()).orElse(null);
        assertThat(findTeam).isNotNull();
        assertThat(findTeam.getStatus()).isEqualTo(Status.ALIVE);

        TeamMember teamMember = teamRepository.findTeamMember(team.getId(), member2.getId()).orElse(null);
        assertThat(teamMember).isNotNull();
        assertThat(teamMember.getStatus()).isEqualTo(Status.DELETE);
    }

    @Test
    void 그룹_퇴장_그룹멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> teamService.exitTeam(member2, team.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 그룹_퇴장_중복() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member1);
        team.addTeamMember(member2);
        em.persist(team);

        teamService.exitTeam(member2, team.getId());
        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> teamService.exitTeam(member2, team.getId())).isInstanceOf(NoAuthorityException.class);
    }

    @Test
    void 그룹_퇴장_남은멤버x_그룹삭제() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        em.flush();
        em.clear();

        //when
        teamService.exitTeam(member1, team.getId());
        em.flush();
        em.clear();

        //then
        Team findTeam = teamRepository.findById(team.getId()).orElse(null);
        assertThat(findTeam).isNotNull();
        assertThat(findTeam.getStatus()).isEqualTo(Status.DELETE);
    }

    @Test
    void 그룹_상세_조회() {
        //given
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2);
        em.persist(team);

        em.flush();
        em.clear();

        //when
        TeamDetailResDto result = teamService.getTeamDetail(member1, team.getId());

        //then
        assertThat(result.getGroupId()).isEqualTo(team.getId());
        assertThat(result.getGroupName()).isEqualTo(team.getTeamName());

        List<TeamMemberResDto> teamMemberDtos = result.getMembers();
        assertThat(teamMemberDtos.size()).isEqualTo(2);
        assertThat(teamMemberDtos).extracting("memberId").containsExactly(member1.getId(), member2.getId());
    }

    @Test
    void 그룹_상세_조회_그룹멤버x() {
        //given
        Team team = Team.create(member1, "team1");
        em.persist(team);

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> teamService.getTeamDetail(member2, team.getId())).isInstanceOf(NoAuthorityException.class);
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

        Schedule scheduleA1 = Schedule.create(member1, new TeamValue(teamA), "scheduleA1", LocalDateTime.now().minusDays(1),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleA1.setTerm(LocalDateTime.now());
        em.persist(scheduleA1);

        Schedule scheduleB1 = Schedule.create(member2, new TeamValue(teamB), "scheduleB1", LocalDateTime.now().minusDays(2),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB1.setTerm(LocalDateTime.now());
        em.persist(scheduleB1);

        Schedule scheduleB2 = Schedule.create(member2, new TeamValue(teamB), "scheduleB2", LocalDateTime.now().minusDays(3),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB2.setTerm(LocalDateTime.now());
        em.persist(scheduleB2);

        Schedule scheduleB3 = Schedule.create(member2, new TeamValue(teamB), "scheduleB3", LocalDateTime.now().minusDays(4),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB3.setTerm(LocalDateTime.now());
        em.persist(scheduleB3);


        Schedule scheduleC1 = Schedule.create(member2, new TeamValue(teamC), "scheduleC1", LocalDateTime.now(),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleC1.setTerm(LocalDateTime.now());
        em.persist(scheduleC1);

        em.flush();
        em.clear();

        //when
        int page = 1;
        DataResDto<List<TeamEachListResDto>> result = teamService.getTeamList(member1, page);

        //then
        List<TeamEachListResDto> data = result.getData();
        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting("groupId").containsExactly(teamC.getId(), teamA.getId(), teamB.getId());
        assertThat(data).extracting("memberSize").containsExactly(3, 1, 2);
    }

    @Test
    void 이벤트핸들러_그룹_지각_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = Schedule.create(member1, new TeamValue(team), "schedule1",
                LocalDateTime.now().minusDays(1), new Location("loc", 1.0, 1.0), 0);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(0), LocalDateTime.now().minusDays(1).plusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(1), LocalDateTime.now().minusDays(1).minusMinutes(10));
        schedule1.arriveScheduleMember(schedule1.getScheduleMembers().get(2), LocalDateTime.now().minusDays(1).plusMinutes(15));
        schedule1.setTerm(schedule1.getScheduleTime().plusMinutes(30));
        em.persist(schedule1);

        Schedule schedule2 = Schedule.create(member1, new TeamValue(team), "schedule2",
                LocalDateTime.now().minusDays(1), new Location("loc", 1.0, 1.0), 0);
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(0), LocalDateTime.now().minusDays(1).plusMinutes(20));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(1), LocalDateTime.now().minusDays(1).minusMinutes(10));
        schedule2.arriveScheduleMember(schedule2.getScheduleMembers().get(2), LocalDateTime.now().minusDays(1).minusMinutes(30));
        schedule2.setTerm(schedule2.getScheduleTime().plusMinutes(30));
        em.persist(schedule2);

        em.flush();
        em.clear();

        //when
        teamService.analyzeLateTimeResult(schedule1.getId());
        em.flush();
        em.clear();

        //then
        Team findTeam = teamRepository.findById(team.getId()).orElse(null);
        assertThat(findTeam).isNotNull();

        TeamLateTimeResult result = objectMapper.readValue(findTeam.getTeamResult().getLateTimeResult(), TeamLateTimeResult.class);
        List<TeamResultMember> lateMemberRanking = result.getMembers();
        assertThat(lateMemberRanking).extracting("memberId").containsExactly(member1.getId(), member3.getId());
        assertThat(lateMemberRanking).extracting("analysis").containsExactly(-30, -15);
    }

    @Test
    void 이벤트핸들러_베팅_분석() throws JsonProcessingException {
        //given
        Team team = Team.create(member1, "team");
        team.addTeamMember(member2);
        team.addTeamMember(member3);
        em.persist(team);

        Schedule schedule1 = Schedule.create(member1, new TeamValue(team), "schedule1",
                LocalDateTime.now().minusDays(1), new Location("loc", 1.0, 1.0), 0);
        schedule1.addScheduleMember(member2, false, 0);
        schedule1.addScheduleMember(member3, false, 0);
        em.persist(schedule1);

        Schedule schedule2 = Schedule.create(member1, new TeamValue(team), "schedule2",
                LocalDateTime.now().minusDays(1), new Location("loc", 1.0, 1.0), 0);
        schedule2.addScheduleMember(member2, false, 0);
        schedule2.addScheduleMember(member3, false, 0);
        em.persist(schedule2);

        Betting betting1 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), 100);
        betting1.setDraw();
        em.persist(betting1);

        Betting betting2 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(1)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting2.setWin(100);
        em.persist(betting2);

        Betting betting3 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(2)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting3.setWin(100);
        em.persist(betting3);

        Betting betting4 = Betting.create(new ScheduleMemberValue(schedule2.getScheduleMembers().get(1)), new ScheduleMemberValue(schedule2.getScheduleMembers().get(2)), 100);
        betting4.setDraw();
        em.persist(betting4);

        Betting betting5 = Betting.create(new ScheduleMemberValue(schedule1.getScheduleMembers().get(2)), new ScheduleMemberValue(schedule1.getScheduleMembers().get(0)), 100);
        betting5.setWin(100);
        em.persist(betting5);

        //when
        teamService.analyzeBettingResult(schedule1.getId());
        em.flush();
        em.clear();

        //then
        Team findTeam = teamRepository.findById(team.getId()).orElse(null);
        assertThat(findTeam).isNotNull();
        System.out.println("findTeam.getTeamResult().getTeamBettingResult() = " + findTeam.getTeamResult().getTeamBettingResult());
        List<TeamResultMember> teamResultMembers = objectMapper.readValue(findTeam.getTeamResult().getTeamBettingResult(), TeamBettingResult.class)
                .getMembers();
        assertThat(teamResultMembers.size()).isEqualTo(3);
        assertThat(teamResultMembers).extracting("memberId").containsExactly(member3.getId(), member2.getId(), member1.getId());
    }
}
