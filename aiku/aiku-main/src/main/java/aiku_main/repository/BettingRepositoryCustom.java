package aiku_main.repository;

import common.domain.value_reference.ScheduleMemberValue;

public interface BettingRepositoryCustom {
    boolean existBettorInSchedule(ScheduleMemberValue bettor, Long scheduleId);
}
