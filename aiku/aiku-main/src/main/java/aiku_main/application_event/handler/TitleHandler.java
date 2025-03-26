package aiku_main.application_event.handler;

import aiku_main.application_event.event.ScheduleCloseEvent;
import aiku_main.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TitleHandler {

    private final TitleService titleService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void checkAllAvailableMemberTitleInSchedule(ScheduleCloseEvent event) {
        checkAllAvailableMemberTitleInSchedule(event.getScheduleId());
    }

    private void checkAllAvailableMemberTitleInSchedule(Long scheduleId) {
        List<Long> memberIds = titleService.getMemberIdsInSchedule(scheduleId);

        // ** 1만 포인트 달성 칭호 **
        titleService.checkAndGivePointsMoreThan10kTitle(memberIds);

        // ** 누적 지각 도착 5회 이상 칭호 **
        titleService.checkAndGiveEarlyArrival10TimesTitle(memberIds);

        // ** 누적 지각 도착 5회 이상 칭호 **
        titleService.checkAndGiveLateArrival5TimesTitle(memberIds);

        // ** 누적 지각 도착 10회 이상 칭호 **
        titleService.checkAndGiveLateArrival10TimesTitle(memberIds);

        // ** 누적 베팅 승리 5회 이상 칭호 **[
        titleService.checkAndGiveBettingWinning5TimesTitle(memberIds);

        // ** 누적 베팅 승리 10회 이상 칭호 **
        titleService.checkAndGiveBettingLosing10TimesTitle(memberIds);
    }
}
