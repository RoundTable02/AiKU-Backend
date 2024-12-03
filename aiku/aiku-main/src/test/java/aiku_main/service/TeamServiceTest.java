package aiku_main.service;

import aiku_main.application_event.publisher.TeamEventPublisher;
import aiku_main.dto.*;
import aiku_main.dto.team.TeamAddDto;
import aiku_main.dto.team.TeamDetailResDto;
import aiku_main.dto.team.TeamResDto;
import aiku_main.dto.team.TeamMemberResDto;
import aiku_main.repository.BettingQueryRepository;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.ScheduleQueryRepository;
import aiku_main.repository.TeamQueryRepository;
import common.domain.member.Member;
import common.domain.team.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    TeamQueryRepository teamQueryRepository;
    @Mock
    ScheduleQueryRepository scheduleQueryRepository;
    @Mock
    BettingQueryRepository bettingQueryRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    TeamEventPublisher teamEventPublisher;

    @InjectMocks
    TeamService teamService;

    @Test
    void addTeam() {
        //given
        Member member = new Member("member1");

        when(memberRepository.findByIdAndStatus(nullable(Long.class), any())).thenReturn(Optional.of(member));

        //when
        TeamAddDto teamDto = new TeamAddDto("team1");
        Long teamId = teamService.addTeam(member.getId(), teamDto);
    }

    @Test
    void enterTeam() {
        //given
        Member member = new Member("member1");
        Team team = Team.create(member, "team1");

        when(teamQueryRepository.existTeamMember(any(), any())).thenReturn(false);
        when(teamQueryRepository.findByIdAndStatus(any(), any())).thenReturn(Optional.of(team));
        when(memberRepository.findByIdAndStatus(nullable(Long.class), any())).thenReturn(Optional.of(member));

        //when
        Long resultId = teamService.enterTeam(member.getId(), null);
    }

    @Test
    void getTeamList() {
        //given
        Member member1 = new Member("member1");

        List<TeamResDto> data = new ArrayList<>();
        when(teamQueryRepository.getTeamList(nullable(Long.class), nullable(Integer.class))).thenReturn(data);

        //when
        int page = 3;
        DataResDto<List<TeamResDto>> result = teamService.getTeamList(member1.getId(), 3);

        //then
        assertThat(result.getPage()).isEqualTo(3);
        assertThat(result.getData()).isEqualTo(data);
    }
}