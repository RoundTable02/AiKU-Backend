package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleAutoCloseEvent;
import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.application_event.event.ScheduleExitEvent;
import aiku_main.service.BettingService;
import aiku_main.service.RacingService;
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
public class RacingHandler {

    private final RacingService racingService;
    private final ScheduleService scheduleService;

    @Async
    @EventListener
    public void analyzeScheduleBettingResult(ScheduleAutoCloseEvent event){
        if(scheduleService.isScheduleAutoClosed(event.getSchedule().getId())){
            racingService.analyzeScheduleRacingResult(event.getSchedule().getId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleBettingResult(ScheduleCloseEvent event){
        racingService.analyzeScheduleRacingResult(event.getSchedule().getId());
    }
}
