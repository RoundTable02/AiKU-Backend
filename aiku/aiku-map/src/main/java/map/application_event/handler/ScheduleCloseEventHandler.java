package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.ScheduleCloseEvent;
import map.service.MapService;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduleCloseEventHandler {

    private final RacingService racingService;
    private final MapService mapService;

    @Async
    @EventListener
    public void terminateRunningRacing(ScheduleCloseEvent event){
        racingService.terminateRunningRacing(event.getScheduleId());
    }

    @Async
    @EventListener
    public void deleteAllLocationsInSchedule(ScheduleCloseEvent event){
        mapService.deleteAllLocationsInSchedule(event.getScheduleId());
    }
}
