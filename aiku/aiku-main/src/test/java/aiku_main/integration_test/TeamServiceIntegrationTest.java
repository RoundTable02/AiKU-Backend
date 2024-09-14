package aiku_main.integration_test;

import aiku_main.dto.TeamAddDto;
import aiku_main.dto.TeamDetailResDto;
import aiku_main.dto.TeamMemberResDto;
import aiku_main.repository.TeamRepository;
import aiku_main.service.TeamService;
import common.domain.Member;
import common.domain.Team;
import common.domain.TeamMember;
import common.exception.NoAuthorityException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("그룹 등록")
    void addTeam() {
        //given
        Member member = Member.create("member1");
        em.persist(member);

        //when
        TeamAddDto teamDto = new TeamAddDto("group1");
        Long teamId = teamService.addTeam(member, teamDto);

        em.flush();
        em.clear();

        //then
        Team team = teamRepository.findById(teamId).get();
        assertThat(team.getTeamName()).isEqualTo(teamDto.getGroupName());
        assertThat(team.getTeamMembers().size()).isEqualTo(1);

        TeamMember teamMember = team.getTeamMembers().get(0);
        assertThat(teamMember.isOwner()).isTrue();
        assertThat(teamMember.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("그룹 입장")
    void test() {
        //given
        Member member = Member.create("member1");
        Member enterMember = Member.create("enterMember");
        em.persist(member);
        em.persist(enterMember);

        Team team = Team.create(member, "team1");
        em.persist(team);

        em.flush();
        em.clear();

        //when
        Long teamId = teamService.enterTeam(enterMember, team.getId());

        //then
        TeamMember teamMember = teamRepository.findTeamMemberByTeamIdAndMemberId(teamId, enterMember.getId()).orElse(null);
        assertThat(teamMember).isNotNull();
        assertThat(teamMember.isOwner()).isFalse();
    }

    @Test
    @DisplayName("그룹 상세 조회-권한O/X")
    void getGroupDetail() {
        //given
        Member member = Member.create("member1");
        Member member2 = Member.create("member2");
        Member noMember = Member.create("noMember");
        em.persist(member);
        em.persist(member2);
        em.persist(noMember);

        Team team = Team.create(member, "team1");
        team.addTeamMember(member2, false);
        em.persist(team);

        em.flush();
        em.clear();

        //when
        TeamDetailResDto result = teamService.getTeamDetail(member, team.getId());

        //then
        assertThat(result.getGroupId()).isEqualTo(team.getId());
        assertThat(result.getGroupName()).isEqualTo(team.getTeamName());

        List<TeamMemberResDto> teamMemberDtos = result.getMembers();
        assertThat(teamMemberDtos.size()).isEqualTo(2);
        assertThat(teamMemberDtos).extracting("memberId").containsExactly(member.getId(), member2.getId());

        //권한x
        assertThatThrownBy(() -> teamService.getTeamDetail(noMember, team.getId())).isInstanceOf(NoAuthorityException.class);
    }
}
