package aiku_main.repository;

import aiku_main.dto.TeamEachListResDto;
import common.domain.team.Team;

import java.util.List;
import java.util.Optional;

public interface TeamReadRepository {
    Optional<Team> findTeamWithMember(Long teamId);
    List<TeamEachListResDto> getTeamList(Long memberId, int page);
}
