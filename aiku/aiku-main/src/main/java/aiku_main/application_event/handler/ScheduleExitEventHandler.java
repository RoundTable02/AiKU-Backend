package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.betting.BettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ScheduleExitEventHandler {

    private final BettingService bettingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteBettingForBettor(ScheduleExitEvent event){
        bettingService.deleteBettingForBettor(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteBettingForBetee(ScheduleExitEvent event){
        bettingService.deleteBettingForBetee(event.getMemberId(), event.getScheduleMemberId(), event.getScheduleId());
    }
}
