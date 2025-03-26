package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.log.*;
import common.domain.value_reference.*;
import org.springframework.stereotype.Component;

@Component
public class PointLogFactory {

    public PointLog createPointLog(PointChangeReason reason, Long memberId, PointChangeType pointChangeType, int pointAmount, Long reasonId) {
        // 포인트 부호
        int signedPointAmount = (pointChangeType.equals(PointChangeType.PLUS)) ? pointAmount : (-1) * pointAmount;

        if (reason.equals(PointChangeReason.SCHEDULE_ENTER)) {
            return new SchedulePointLog(memberId,
                    signedPointAmount,
                    "스케줄 입장료 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new ScheduleValue(reasonId));
        } else if (reason.equals(PointChangeReason.SCHEDULE_EXIT)) {
            return new SchedulePointLog(memberId,
                    signedPointAmount,
                    "스케줄 퇴장 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new ScheduleValue(reasonId));
        } else if (reason.equals(PointChangeReason.SCHEDULE_REWARD)) {
            return new SchedulePointLog(memberId,
                    signedPointAmount,
                    "스케줄 성실 참가 보상 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new ScheduleValue(reasonId));
        } else if (reason.equals(PointChangeReason.BETTING)) {
            return new BettingPointLog(memberId,
                    signedPointAmount,
                    "베팅금 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new BettingValue(reasonId));
        } else if (reason.equals(PointChangeReason.BETTING_CANCLE)) {
            return new BettingPointLog(memberId,
                    signedPointAmount,
                    "베팅 취소 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new BettingValue(reasonId));
        } else if (reason.equals(PointChangeReason.BETTING_REWARD)) {
            return new BettingPointLog(memberId,
                    signedPointAmount,
                    "베팅 결과 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new BettingValue(reasonId));
        } else if (reason.equals(PointChangeReason.RACING)) {
            return new RacingPointLog(memberId,
                    signedPointAmount,
                    "레이싱 대결 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new RacingValue(reasonId));
        } else if (reason.equals(PointChangeReason.RACING_CANCEL)) {
            return new RacingPointLog(memberId,
                    signedPointAmount,
                    "레이싱 취소 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new RacingValue(reasonId));
        } else if (reason.equals(PointChangeReason.SHOP)) {
            return new ShopPointLog(memberId,
                    signedPointAmount,
                    "아쿠 상점 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new ShopProductValue(reasonId));
        } else if (reason.equals(PointChangeReason.SHOP_CANCEL)) {
            return new ShopPointLog(memberId,
                    signedPointAmount,
                    "상점 취소 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new ShopProductValue(reasonId));
        } else if (reason.equals(PointChangeReason.EVENT)) {
            return new EventLog(memberId,
                    signedPointAmount,
                    "이벤트 보상 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new EventValue(reasonId));
        } else if (reason.equals(PointChangeReason.EVENT_CANCEL)) {
            return new EventLog(memberId,
                    signedPointAmount,
                    "이벤트 보상 : " + signedPointAmount + " 아쿠",
                    PointLogStatus.ACCEPT,
                    new EventValue(reasonId));
        }

        return null;
    }
}
