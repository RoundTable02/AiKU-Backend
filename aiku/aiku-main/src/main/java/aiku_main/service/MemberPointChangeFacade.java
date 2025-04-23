package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.application_event.publisher.PointChangeFailEventPublisher;
import aiku_main.exception.PointChangeFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberPointChangeFacade {

    private final MemberService memberService;
    private final PointLogService pointLogService;
    private final PointChangeFailEventPublisher pointChangeFailEventPublisher;

    @Transactional
    public void makePointChange(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        try {
            // 멤버 포인트 변화
            memberService.updateMemberPoint(memberId, pointChangeType, pointAmount);
            // 로그 기록
            pointLogService.savePointLog(pointChangeReason, memberId, pointChangeType, pointAmount, reasonId);
        } catch (Exception e) {
            // 실패 이벤트 publish
            pointChangeFailEventPublisher.publish(memberId, pointChangeType, pointAmount, pointChangeReason, reasonId);

            throw new PointChangeFailException();
        }

    }

}
