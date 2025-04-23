package aiku_main.repository.racing;

import aiku_main.repository.dto.TeamRacingResultMemberDto;
import common.domain.racing.Racing;

import java.util.List;
import java.util.Map;

public interface RacingRepositoryCustom {
    Map<Long, List<TeamRacingResultMemberDto>> findMemberWithTermRacingsInTeam(Long teamId);

    List<Racing> findTermRacingsInSchedule(Long scheduleId);
}
