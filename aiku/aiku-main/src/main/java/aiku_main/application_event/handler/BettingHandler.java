package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.BettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class BettingHandler {

    private final BettingService bettingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleExitEvent(ScheduleExitEvent event){
        bettingService.exitSchedule_deleteBettingForBettor(event.getMember().getId(), event.getScheduleMember().getId(), event.getSchedule().getId());
        bettingService.exitSchedule_deleteBettingForBetee(event.getMember().getId(), event.getScheduleMember().getId(), event.getSchedule().getId());
    }
}
