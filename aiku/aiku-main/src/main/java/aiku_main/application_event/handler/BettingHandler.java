package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.betting.BettingService;
import aiku_main.service.schedule.ScheduleResultAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class BettingHandler {

    private final BettingService bettingService;
    private final ScheduleResultAnalysisService scheduleResultAnalysisService;

}
