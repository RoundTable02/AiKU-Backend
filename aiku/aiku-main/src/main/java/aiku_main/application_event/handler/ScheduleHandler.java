package aiku_main.application_event.handler;

import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ScheduleHandler {

    private final ScheduleService scheduleService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamExitEvent(TeamExitEvent event){
        scheduleService.exitAllScheduleInTeam(event.getMember().getId(), event.getTeam().getId());
    }
}
