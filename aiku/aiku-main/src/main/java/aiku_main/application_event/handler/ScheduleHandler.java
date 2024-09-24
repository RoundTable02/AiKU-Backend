package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleOpenEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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

    @EventListener
    public void handleScheduleOpenEvent(ScheduleOpenEvent event){
        scheduleService.scheduleOpen(event.getScheduleId());
    }

    @Async
    @EventListener
    public void handleScheduleAutoCloseEvent(ScheduleAutoCloseEvent event){
        scheduleService.scheduleAutoClose(event.getScheduleId());
        scheduleService.processScheduleResultPoint(event.getScheduleId());
        scheduleService.analyzeScheduleArrivalResult(event.getScheduleId());
    }
}
