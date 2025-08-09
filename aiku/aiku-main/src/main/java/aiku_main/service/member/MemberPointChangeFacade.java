package aiku_main.service.member;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.service.log.PointLogService;
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

    @Transactional
    public void makePointChange(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // 멤버 포인트 변화
        memberService.updateMemberPoint(memberId, pointChangeType, pointAmount);
        // 로그 기록
        pointLogService.savePointLog(pointChangeReason, memberId, pointChangeType, pointAmount, reasonId);

    }

}
