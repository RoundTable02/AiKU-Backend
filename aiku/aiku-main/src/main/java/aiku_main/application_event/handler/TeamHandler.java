package aiku_main.application_event.handler;

import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.team.TeamResultAnalysisService;
import aiku_main.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;
    private final TeamResultAnalysisService teamResultAnalysisService;



    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateLateTimeResultOfExitMember(TeamExitEvent event) {
        teamResultAnalysisService.updateTeamResultOfExitMember(event.getMemberId(), event.getTeamId());
    }
}
