package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.AskRacingEvent;
import map.scheduler.RacingScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class AskRacingEventHandler {

    private final RacingScheduler racingScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void checkRacingStatus30secsLater(AskRacingEvent event){
        racingScheduler.checkRacingStatus30secsLater(event.getRacingInfo());
    }
}
