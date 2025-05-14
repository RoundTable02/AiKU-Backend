package aiku_main.repository.racing;

import aiku_main.dto.schedule.result.racing.RacingResult;

import java.util.List;
import java.util.Map;

public interface RacingRepositoryCustom {

    List<RacingResult> getRacingResultInSchedule(Long scheduleId);
}
