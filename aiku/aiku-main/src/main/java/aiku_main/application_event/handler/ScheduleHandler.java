package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleOpenEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.schedule.ScheduleService;
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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamExitEvent(TeamExitEvent event){
        scheduleService.exitAllScheduleInTeam(event.getMemberId(), event.getTeamId());
    }

    @Async
    @EventListener
    public void handleScheduleOpenEvent(ScheduleOpenEvent event){
        scheduleService.openSchedule(event.getScheduleId());
    }

    @Async
    @EventListener
    public void closeScheduleAuto(ScheduleAutoCloseEvent event){
        scheduleService.closeScheduleAuto(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processSchedulePoint(ScheduleCloseEvent event){
        scheduleService.processScheduleResultPoint(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleArrivalResult(ScheduleCloseEvent event) {
//        scheduleService.analyzeScheduleArrivalResult(event.getScheduleId());
    }
}
