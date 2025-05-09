package aiku_main.service.schedule;

import aiku_main.dto.schedule.result.betting.BettingResult;
import aiku_main.dto.schedule.result.betting.BettingResultDto;
import aiku_main.dto.schedule.ScheduleArrivalMember;
import aiku_main.dto.schedule.ScheduleArrivalResult;
import aiku_main.repository.betting.BettingRepository;
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

    @Transactional
    public void analyzeScheduleArrivalResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleWithResult(scheduleId).orElseThrow();

        List<ScheduleArrivalMember> arrivalMembers = scheduleRepository.getScheduleArrivalResults(scheduleId);
        ScheduleArrivalResult arrivalResult = new ScheduleArrivalResult(scheduleId, arrivalMembers);
        schedule.setScheduleArrivalResult(ObjectMapperUtil.toJson(arrivalResult));
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
}
