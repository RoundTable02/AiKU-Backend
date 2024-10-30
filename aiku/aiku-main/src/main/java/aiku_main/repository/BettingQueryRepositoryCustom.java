package aiku_main.repository;

import aiku_main.repository.dto.TeamBettingResultMemberDto;
import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.value_reference.ScheduleMemberValue;

import java.util.List;
import java.util.Map;

public interface BettingQueryRepositoryCustom {

    List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus);

    boolean existBettorInSchedule(ScheduleMemberValue bettor, Long scheduleId);

    Map<Long, List<TeamBettingResultMemberDto>> findMemberTermBettingsInTeam(Long teamId);
}
