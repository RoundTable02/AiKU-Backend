package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.MemberArrivalEvent;
import map.service.MapService;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MemberArrivalEventHandler {

    private final RacingService racingService;
    private final MapService mapService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void makeMemberWinnerInRacing(MemberArrivalEvent event){
        racingService.makeMemberWinnerInRacing(event.getMemberId(), event.getScheduleId(), event.getScheduleName());
    }

    @EventListener
    public void sendKafkaAlarmIfMemberArrived(MemberArrivalEvent event) {
        mapService.sendKafkaAlarmIfMemberArrived(event.getMemberId(),
                event.getScheduleId(),
                event.getScheduleName(),
                event.getArrivalTime()
        );
    }
}
