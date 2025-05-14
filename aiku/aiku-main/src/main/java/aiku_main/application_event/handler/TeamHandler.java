package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.team.TeamResultAnalysisService;
import aiku_main.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;
    private final TeamResultAnalysisService teamResultAnalysisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeLateTimeResult(ScheduleCloseEvent event){
        teamResultAnalysisService.analyzeLateTimeResult(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeBettingResult(ScheduleCloseEvent event){
//        teamService.analyzeBettingResult(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeRacingResult(ScheduleCloseEvent event){
//        teamService.analyzeRacingResult(event.getScheduleId());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateLateTimeResultOfExitMember(TeamExitEvent event) {
        teamService.updateTeamResultOfExitMember(event.getMemberId(), event.getTeamId());
    }
}
