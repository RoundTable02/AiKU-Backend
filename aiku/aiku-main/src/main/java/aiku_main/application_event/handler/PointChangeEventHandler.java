package aiku_main.application_event.handler;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.service.MemberService;
import aiku_main.service.PointLogFactory;
import aiku_main.service.PointLogService;
import common.domain.log.PointLog;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PointChangeEventHandler {

    private final MemberService memberService;
    private final PointLogService pointLogService;
    private final PointLogFactory pointLogFactory;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pointChangeEvent(PointChangeEvent event) {
        // 멤버 포인트 변화
        memberService.updateMemberPoint(event.getMember().getId(), event.getSign(), event.getPointAmount());

        // 로그 기록
        PointLog pointLog = pointLogFactory.createPointLog(event.getReason(),
                event.getMember().getId(),
                event.getSign(),
                event.getPointAmount(),
                event.getReasonId());
        pointLogService.savePointLog(pointLog);
    }
}
