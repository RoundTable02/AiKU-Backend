package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.MemberArrivalEvent;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MemberArrivalEventHandler {

    private final RacingService racingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void makeMemberWinnerInRacing(MemberArrivalEvent event){
        racingService.makeMemberWinnerInRacing(event.getMemberId(), event.getScheduleId(), event.getScheduleName());
    }
}
