package aiku_main.application_event.handler;

import aiku_main.application_event.event.PointChangeFailEvent;
import aiku_main.service.PointChangeFailSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PointChangeFailEventHandler {

    private final PointChangeFailSagaService pointChangeFailSagaService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pointChangeFailEvent(PointChangeFailEvent event) {
        pointChangeFailSagaService.notifyAndRollbackPointChange(event.getMember(), event.getSign(), event.getPointAmount(), event.getReason(), event.getReasonId());
    }
}
