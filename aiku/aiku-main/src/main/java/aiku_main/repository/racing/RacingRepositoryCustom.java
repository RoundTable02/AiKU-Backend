package aiku_main.repository.racing;

import aiku_main.dto.schedule.result.racing.RacingResult;
import aiku_main.repository.dto.TeamRacingResultMemberDto;

import java.util.List;
import java.util.Map;

public interface RacingRepositoryCustom {
    Map<Long, List<TeamRacingResultMemberDto>> findMemberWithTermRacingsInTeam(Long teamId);

    List<RacingResult> getRacingResultInSchedule(Long scheduleId);
}
