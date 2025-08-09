package aiku_main.repository.betting;

import aiku_main.dto.schedule.result.betting.BettingResult;
import common.domain.betting.Betting;
import common.domain.ExecStatus;

import java.util.List;

public interface BettingRepositoryCustom {

    List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus);

    boolean existBettorInSchedule(Long scheduleMemberIdOfBettor, Long scheduleId);

    List<BettingResult> getBettingResultsInSchedule(Long scheduleId);
}
