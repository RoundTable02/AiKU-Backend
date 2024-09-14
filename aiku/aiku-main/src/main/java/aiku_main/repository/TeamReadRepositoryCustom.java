package aiku_main.repository;

import aiku_main.dto.TeamEachListResDto;
import common.domain.Team;

import java.util.List;
import java.util.Optional;

public interface TeamReadRepositoryCustom {
    Optional<Team> findTeamWithMember(Long teamId);
    List<TeamEachListResDto> getTeamList(Long memberId, int page);
}
