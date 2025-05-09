package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.service.schedule.ScheduleResultAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class RacingHandler {

    private ScheduleResultAnalysisService scheduleResultAnalysisService;
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleBettingResult(ScheduleCloseEvent event){
        scheduleResultAnalysisService.analyzeRacingResult(event.getScheduleId());
    }
}
