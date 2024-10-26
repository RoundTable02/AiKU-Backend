package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.AskRacingEvent;
import map.application_event.event.RacingStatusNotChangedEvent;
import map.scheduler.RacingScheduler;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class RacingHandler {

    private final RacingService racingService;
    private final RacingScheduler racingScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAskRacingEvent(AskRacingEvent event){
        racingScheduler.checkRacingStatus30secsLater(event.getRacingInfo());
    }

    @EventListener
    public void handleRacingStatusNotChangedEvent(RacingStatusNotChangedEvent event){
        racingService.autoDeleteRacingById(event.getRacingInfo());
    }

}
