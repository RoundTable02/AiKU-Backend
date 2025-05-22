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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleExitEvent(ScheduleExitEvent event){
        bettingService.exitSchedule_deleteBettingForBettor(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
        bettingService.exitSchedule_deleteBettingForBetee(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
    }
}
