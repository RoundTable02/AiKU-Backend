package aiku_main.repository.betting;

import aiku_main.dto.schedule.result.betting.BettingResult;
import aiku_main.repository.dto.TeamBettingResultMemberDto;
import common.domain.betting.Betting;
import common.domain.ExecStatus;

import java.util.List;
import java.util.Map;

public interface BettingRepositoryCustom {

    List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus);

    boolean existBettorInSchedule(Long scheduleMemberIdOfBettor, Long scheduleId);

    Map<Long, List<TeamBettingResultMemberDto>> findMemberTermBettingsInTeam(Long teamId);
    List<BettingResult> getBettingResultsInSchedule(Long scheduleId);
}
