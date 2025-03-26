package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.BettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class BettingHandler {

    private final BettingService bettingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleExitEvent(ScheduleExitEvent event){
        bettingService.exitSchedule_deleteBettingForBettor(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
        bettingService.exitSchedule_deleteBettingForBetee(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processBettingResult(ScheduleCloseEvent event){
        bettingService.processBettingResult(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleBettingResult(ScheduleCloseEvent event){
        bettingService.analyzeScheduleBettingResult(event.getScheduleId());
    }
}
