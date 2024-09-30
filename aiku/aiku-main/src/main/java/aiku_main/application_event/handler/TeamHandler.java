package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;

    @EventListener
    public void handleScheduleAutoCloseEvent(ScheduleAutoCloseEvent event){
        teamService.analyzeLateTimeResult(event.getSchedule().getId());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamExitEvent(TeamExitEvent event) {
        teamService.updateLateTimeResultOfExitMember(event.getMember().getId(), event.getTeam().getId());
    }
}
