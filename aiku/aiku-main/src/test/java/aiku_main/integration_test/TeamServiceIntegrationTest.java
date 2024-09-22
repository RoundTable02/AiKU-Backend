package aiku_main.integration_test;

import aiku_main.dto.*;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.TeamRepository;
import aiku_main.service.TeamService;
import common.domain.*;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamMember;
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
        scheduleA1.setScheduleStatus(ExecStatus.TERM);
        em.persist(scheduleA1);

        Schedule scheduleB1 = Schedule.create(member2, new TeamValue(teamB), "scheduleB1", LocalDateTime.now().minusDays(2),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB1.setScheduleStatus(ExecStatus.TERM);
        em.persist(scheduleB1);

        Schedule scheduleB2 = Schedule.create(member2, new TeamValue(teamB), "scheduleB2", LocalDateTime.now().minusDays(3),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB2.setScheduleStatus(ExecStatus.TERM);
        em.persist(scheduleB2);

        Schedule scheduleB3 = Schedule.create(member2, new TeamValue(teamB), "scheduleB3", LocalDateTime.now().minusDays(4),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleB3.setScheduleStatus(ExecStatus.TERM);
        em.persist(scheduleB3);


        Schedule scheduleC1 = Schedule.create(member2, new TeamValue(teamC), "scheduleC1", LocalDateTime.now(),
                new Location("loc1", 1.1, 1.1), 0);
        scheduleC1.setScheduleStatus(ExecStatus.WAIT);
        em.persist(scheduleC1);

        em.flush();
        em.clear();

        //when
        int page = 1;
        DataResDto<List<TeamEachListResDto>> result = teamService.getTeamList(member1, page);

        //then
        assertThat(result.getTotalCount()).isEqualTo(3);
        List<TeamEachListResDto> data = result.getData();
        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting("groupId").containsExactly(teamA.getId(), teamB.getId(), teamC.getId());
        assertThat(data).extracting("memberSize").containsExactly(1, 2, 3);
    }
}
