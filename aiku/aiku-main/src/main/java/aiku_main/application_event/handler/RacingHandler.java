package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class RacingHandler {

    private final RacingService racingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleBettingResult(ScheduleCloseEvent event){
        racingService.analyzeScheduleRacingResult(event.getScheduleId());
    }
}
