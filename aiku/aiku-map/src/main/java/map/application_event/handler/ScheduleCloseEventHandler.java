package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.ScheduleCloseEvent;
import map.service.MapService;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ScheduleCloseEventHandler {

    private final RacingService racingService;
    private final MapService mapService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void terminateRunningRacing(ScheduleCloseEvent event){
        // 레이싱 종료
        racingService.terminateRunningRacing(event.getScheduleId());
        // 이벤트 전달
        mapService.sendKafkaEventIfScheduleClosed(event.getScheduleId(), event.getCloseTime());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteAllLocationsInSchedule(ScheduleCloseEvent event){
        mapService.deleteAllLocationsInSchedule(event.getScheduleId());
    }
}
