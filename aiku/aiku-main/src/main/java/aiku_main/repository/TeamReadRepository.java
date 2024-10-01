package aiku_main.repository;

import aiku_main.application_event.domain.TeamResultMember;
import aiku_main.dto.TeamEachListResDto;
import aiku_main.dto.TotalCountDto;
import common.domain.team.Team;

import java.util.List;
import java.util.Optional;

public interface TeamReadRepository {

    Optional<Team> findTeamWithMember(Long teamId);
    List<TeamEachListResDto> getTeamList(Long memberId, int page);
    List<TeamResultMember> getTeamLateTimeResult(Long teamId);
}
