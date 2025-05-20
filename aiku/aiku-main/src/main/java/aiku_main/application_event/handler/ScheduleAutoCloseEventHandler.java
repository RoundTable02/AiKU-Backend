package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduleAutoCloseEventHandler {

    private final ScheduleService scheduleService;

    @EventListener
    public void closeScheduleAuto(ScheduleAutoCloseEvent event){
        scheduleService.closeScheduleAuto(event.getScheduleId());
    }
}
