package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.BettingService;
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
public class BettingHandler {

    private final BettingService bettingService;
    private final ScheduleService scheduleService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleScheduleExitEvent(ScheduleExitEvent event){
        bettingService.exitSchedule_deleteBettingForBettor(event.getMember().getId(), event.getScheduleMember().getId(), event.getSchedule().getId());
        bettingService.exitSchedule_deleteBettingForBetee(event.getMember().getId(), event.getScheduleMember().getId(), event.getSchedule().getId());
    }

    @Order(3)
    @Async
    @EventListener
    public void processBettingResult(ScheduleAutoCloseEvent event){
        if(scheduleService.isScheduleAutoClosed(event.getSchedule().getId())) {
            bettingService.processBettingResult(event.getSchedule().getId());
        }
    }

    @Async
    @EventListener
    public void analyzeScheduleBettingResult(ScheduleAutoCloseEvent event){
        if(scheduleService.isScheduleAutoClosed(event.getSchedule().getId())){
            bettingService.analyzeScheduleBettingResult(event.getSchedule().getId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processBettingResult(ScheduleCloseEvent event){
        bettingService.processBettingResult(event.getSchedule().getId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleBettingResult(ScheduleCloseEvent event){
        bettingService.analyzeScheduleBettingResult(event.getSchedule().getId());
    }
}
