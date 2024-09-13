package aiku_main.integration_test;

import aiku_main.dto.TeamAddDto;
import aiku_main.repository.TeamRepository;
import aiku_main.service.TeamService;
import common.domain.Member;
import common.domain.Team;
import common.domain.TeamMember;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
}
