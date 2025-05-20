package aiku_main.service.member;

import aiku_main.application_event.event.PointChangeFailEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.exception.PointChangeFailException;
import aiku_main.service.log.PointLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberPointChangeFacade {

    private final MemberService memberService;
    private final PointLogService pointLogService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void makePointChange(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        try {
            // 멤버 포인트 변화
            memberService.updateMemberPoint(memberId, pointChangeType, pointAmount);
            // 로그 기록
            pointLogService.savePointLog(pointChangeReason, memberId, pointChangeType, pointAmount, reasonId);
        } catch (Exception e) {
            // 실패 이벤트 publish
            PointChangeFailEvent event = new PointChangeFailEvent(memberId, pointChangeType, pointAmount, pointChangeReason, reasonId);
            publisher.publishEvent(event);
            
            throw new PointChangeFailException();
        }

    }

}
