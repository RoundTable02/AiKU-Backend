package aiku_main.repository;

import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.value_reference.ScheduleMemberValue;

import java.util.List;
import java.util.Map;

public interface BettingRepositoryCustom {

    boolean existBettorInSchedule(ScheduleMemberValue bettor, Long scheduleId);
    List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus);
}
