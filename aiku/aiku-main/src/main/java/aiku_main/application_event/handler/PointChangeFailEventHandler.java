package aiku_main.application_event.handler;

import aiku_main.application_event.event.PointChangeFailEvent;
import aiku_main.service.PointChangeFailSagaHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PointChangeFailEventHandler {

    private final PointChangeFailSagaHelper pointChangeFailSagaHelper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pointChangeFailEvent(PointChangeFailEvent event) {
        pointChangeFailSagaHelper.process(event.getMember(), event.getSign(), event.getPointAmount(), event.getReason(), event.getReasonId());
    }
}
