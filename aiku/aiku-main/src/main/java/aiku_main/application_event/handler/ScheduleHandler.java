package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleOpenEvent;
import aiku_main.application_event.event.TeamExitEvent;
import aiku_main.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
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
        scheduleService.openSchedule(event.getSchedule().getId());
    }

    @Order(1)
    @Async
    @EventListener
    public void closeScheduleAuto(ScheduleAutoCloseEvent event){
        scheduleService.closeScheduleAuto(event.getSchedule().getId());
    }

    @Order(2)
    @Async
    @EventListener
    public void processSchedulePoint(ScheduleAutoCloseEvent event){
        if(scheduleService.isScheduleAutoClosed(event.getSchedule().getId())) {
            scheduleService.processScheduleResultPoint(event.getSchedule().getId());
        }
    }

    @Async
    @EventListener
    public void analyzeScheduleArrivalResult(ScheduleAutoCloseEvent event){
        if(scheduleService.isScheduleAutoClosed(event.getSchedule().getId())) {
            scheduleService.analyzeScheduleArrivalResult(event.getSchedule().getId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processSchedulePoint(ScheduleCloseEvent event){
        scheduleService.processScheduleResultPoint(event.getSchedule().getId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleArrivalResult(ScheduleCloseEvent event) {
        scheduleService.analyzeScheduleArrivalResult(event.getSchedule().getId());
    }
}
