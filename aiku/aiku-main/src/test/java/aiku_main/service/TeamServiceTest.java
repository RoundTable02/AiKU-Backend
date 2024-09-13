package aiku_main.service;

import aiku_main.dto.TeamAddDto;
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

import java.util.Optional;

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
}