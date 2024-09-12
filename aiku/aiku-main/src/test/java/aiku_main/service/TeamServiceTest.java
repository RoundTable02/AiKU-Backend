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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;

    @InjectMocks
    TeamService teamService;

    @Spy
    Member member;

    @Test
    void addTeam() {
        //given
        Member member = new Member("member1");

        //when
        TeamAddDto teamDto = new TeamAddDto("team1");
        Long teamId = teamService.addTeam(member, teamDto);
    }
}