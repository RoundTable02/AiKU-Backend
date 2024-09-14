package aiku_main.service;

import aiku_main.dto.TeamAddDto;
import aiku_main.dto.TeamDetailResDto;
import aiku_main.dto.TeamMemberResDto;
import aiku_main.repository.TeamRepository;
import common.domain.Member;
import common.domain.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;

    @InjectMocks
    TeamService teamService;

    @Test
    void addTeam() {
        //given
        Member member = new Member("member1");

        //when
        TeamAddDto teamDto = new TeamAddDto("team1");
        Long teamId = teamService.addTeam(member, teamDto);
    }

    @Test
    void enterTeam() {
        //given
        Member member = new Member("member1");
        Team team = Team.create(member, "team1");

        when(teamRepository.existTeamMember(any(), any())).thenReturn(false);
        when(teamRepository.findById(any())).thenReturn(Optional.of(team));

        //when
        Long resultId = teamService.enterTeam(member, null);
    }

    @Test
    void getTeamDetail() {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        Team team = Team.create(member1, "team1");
        team.addTeamMember(member2, false);

        when(teamRepository.existTeamMember(any(), any())).thenReturn(true);
        when(teamRepository.findTeamWithMember(any())).thenReturn(Optional.of(team));

        //when
        TeamDetailResDto result = teamService.getTeamDetail(member1, team.getId());

        //then
        assertThat(result.getGroupName()).isEqualTo(team.getTeamName());

        List<TeamMemberResDto> teamMembers = result.getMembers();
        assertThat(teamMembers).extracting("nickname").containsExactly(member1.getNickname(), member2.getNickname());
    }
}