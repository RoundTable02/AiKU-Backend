package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;

    @EventListener
    public void handleScheduleAutoCloseEvent(ScheduleAutoCloseEvent event){
        teamService.analyzeLateTimeResult(event.getSchedule().getId());
    }
}
