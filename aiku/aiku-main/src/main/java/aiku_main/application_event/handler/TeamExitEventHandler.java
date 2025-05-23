package aiku_main.application_event.handler;

import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.schedule.ScheduleService;
import aiku_main.service.team.TeamResultAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class TeamExitEventHandler {

    private final ScheduleService scheduleService;
    private final TeamResultAnalysisService teamResultAnalysisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateLateTimeResultOfExitMember(TeamExitEvent event) {
        teamResultAnalysisService.updateTeamResultOfExitMember(event.getMemberId(), event.getTeamId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamExitEvent(TeamExitEvent event){
        scheduleService.exitAllScheduleInTeam(event.getMemberId(), event.getTeamId());
    }
}
