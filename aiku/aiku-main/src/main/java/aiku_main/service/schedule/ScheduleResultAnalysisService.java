package aiku_main.service.schedule;

import aiku_main.dto.schedule.result.arrival_time.ScheduleArrivalResult;
import aiku_main.dto.schedule.result.racing.RacingResult;
import aiku_main.dto.schedule.result.racing.RacingResultDto;
import aiku_main.dto.schedule.result.betting.BettingResult;
import aiku_main.dto.schedule.result.betting.BettingResultDto;
import aiku_main.dto.schedule.result.arrival_time.ScheduleArrivalResultDto;
import aiku_main.repository.betting.BettingRepository;
import aiku_main.repository.racing.RacingRepository;
import aiku_main.repository.schedule.ScheduleRepository;
import common.domain.schedule.Schedule;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScheduleResultAnalysisService {

    private final ScheduleRepository scheduleRepository;
    private final BettingRepository bettingRepository;
    private final RacingRepository racingRepository;

    @Transactional
    public void analyzeScheduleArrivalResult(Long scheduleId) {
        List<ScheduleArrivalResult> arrivalResults = scheduleRepository.getScheduleArrivalResults(scheduleId);
        ScheduleArrivalResultDto result = new ScheduleArrivalResultDto(scheduleId, arrivalResults);

        Schedule schedule = scheduleRepository.findScheduleWithResult(scheduleId).orElseThrow();
        schedule.setScheduleArrivalResult(ObjectMapperUtil.toJson(result));
    }

    @Transactional
    public void analyzeBettingResult(Long scheduleId) {
        List<BettingResult> bettingResults = bettingRepository.getBettingResultsInSchedule(scheduleId);
        if(noBettingInSchedule(bettingResults)){
            return;
        }

        BettingResultDto result = new BettingResultDto(scheduleId, bettingResults);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setScheduleBettingResult(ObjectMapperUtil.toJson(result));
    }

    private boolean noBettingInSchedule(List<BettingResult> bettingResults){
        return bettingResults.isEmpty();
    }

    @Transactional
    public void analyzeRacingResult(Long scheduleId) {
        List<RacingResult> racingResults = racingRepository.getRacingResultInSchedule(scheduleId);

        if (noRacingInSchedule(racingResults)) {
            return;
        };

        RacingResultDto result = new RacingResultDto(scheduleId, racingResults);

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setScheduleRacingResult(ObjectMapperUtil.toJson(result));
    }

    private boolean noRacingInSchedule(List<RacingResult> racingResults) {
        return racingResults.isEmpty();
    }
}
