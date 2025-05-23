package aiku_main.application_event.handler;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.service.MemberPointChangeFacade;
import aiku_main.service.MemberService;
import aiku_main.service.PointLogFactory;
import aiku_main.service.PointLogService;
import common.domain.log.PointLog;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PointChangeEventHandler {

    private final MemberPointChangeFacade pointChangeFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pointChangeEvent(PointChangeEvent event) {
        pointChangeFacade.makePointChange(event.getMemberId(), event.getSign(), event.getPointAmount(), event.getReason(), event.getReasonId());
    }
}
