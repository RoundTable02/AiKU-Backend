package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class TeamHandler {

    private final TeamService teamService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeLateTimeResult(ScheduleCloseEvent event){
        teamService.analyzeLateTimeResult(event.getSchedule().getId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeBettingResult(ScheduleCloseEvent event){
        teamService.analyzeBettingResult(event.getSchedule().getId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeRacingResult(ScheduleCloseEvent event){
        teamService.analyzeRacingResult(event.getSchedule().getId());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateLateTimeResultOfExitMember(TeamExitEvent event) {
        teamService.updateTeamResultOfExitMember(event.getMember().getId(), event.getTeam().getId());
    }
}
