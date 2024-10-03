package aiku_main.repository;

import aiku_main.repository.dto.TeamBettingResultMemberDto;
import common.domain.Betting;

import java.util.List;
import java.util.Map;

public interface BettingReadRepository {
    Map<Long, List<TeamBettingResultMemberDto>> findMemberTermBettingsInTeam(Long teamId);
}
