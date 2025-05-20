package map.application_event.handler;

import lombok.RequiredArgsConstructor;
import map.application_event.event.RacingStatusNotChangedEvent;
import map.service.RacingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RacingStatusNotChangedEventHandler {

    private final RacingService racingService;

    @EventListener
    public void autoDeleteRacingById(RacingStatusNotChangedEvent event){
        racingService.autoDeleteRacingById(event.getRacingInfo());
    }
}
