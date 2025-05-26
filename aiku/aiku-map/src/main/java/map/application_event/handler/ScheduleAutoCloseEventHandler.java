package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.ScheduleAutoCloseEvent;
import map.application_event.event.ScheduleCloseEvent;
import map.service.MapService;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduleAutoCloseEventHandler {

    private final RacingService racingService;
    private final MapService mapService;

    @Async
    @EventListener
    public void terminateRunningRacing(ScheduleAutoCloseEvent event){
        // 멤버 자동 도착
        mapService.makeNotArrivedMemberArrive(event.getScheduleId(), event.getCloseTime());
        // 레이싱 종료
        racingService.terminateRunningRacing(event.getScheduleId());
        // 이벤트 전달
        mapService.sendKafkaEventIfScheduleClosed(event.getScheduleId(), event.getCloseTime());
    }

    @Async
    @EventListener
    public void deleteAllLocationsInSchedule(ScheduleAutoCloseEvent event){
        mapService.deleteAllLocationsInSchedule(event.getScheduleId());
    }
}
