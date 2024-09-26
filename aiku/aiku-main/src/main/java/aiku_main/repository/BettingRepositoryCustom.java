package aiku_main.repository;

import common.domain.Betting;
import common.domain.value_reference.ScheduleMemberValue;

import java.util.List;

public interface BettingRepositoryCustom {
    boolean existBettorInSchedule(ScheduleMemberValue bettor, Long scheduleId);
    List<Betting> findBettingsInSchedule(Long scheduleId);
}
