package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.service.betting.BettingService;
import aiku_main.service.schedule.ScheduleResultAnalysisService;
import aiku_main.service.schedule.ScheduleService;
import aiku_main.service.team.TeamResultAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 1. schedule-close 토픽 consume
 * 2. ScheduleService에서 스케줄 종료(트랜잭션)
 * 3. ScheduleCloseEvent 발행
 */
@RequiredArgsConstructor
@Component
public class ScheduleCloseEventHandler {

    private final ScheduleService scheduleService;
    private final ScheduleResultAnalysisService scheduleResultAnalysisService;
    private final BettingService bettingService;
    private final TeamResultAnalysisService teamResultAnalysisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processSchedulePoint(ScheduleCloseEvent event){
        scheduleService.processScheduleResultPoint(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void termBettingAndProcessAndAnalysisResult(ScheduleCloseEvent event){
        bettingService.termBettingAndProcessResultPoint(event.getScheduleId());
        scheduleResultAnalysisService.analyzeBettingResult(event.getScheduleId());
        teamResultAnalysisService.analyzeBettingResult(event.getTeamId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleArrivalResult(ScheduleCloseEvent event) {
        scheduleResultAnalysisService.analyzeScheduleArrivalResult(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeScheduleRacingResult(ScheduleCloseEvent event){
        scheduleResultAnalysisService.analyzeRacingResult(event.getScheduleId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeTeamLateTimeResult(ScheduleCloseEvent event){
        teamResultAnalysisService.analyzeLateTimeResult(event.getTeamId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void analyzeTeamRacingResult(ScheduleCloseEvent event){
        teamResultAnalysisService.analyzeRacingResult(event.getTeamId());
    }
}
