package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.value_reference.MemberValue;
import org.springframework.stereotype.Component;

@Component
public class PointChangeFailSagaHelper {


    public void process(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        switch (pointChangeReason) {
            case SCHEDULE_ENTER:
                makeScheduleEnterRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case SCHEDULE_EXIT:
                makeScheduleExitRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case SCHEDULE_REWARD:
                makeScheduleRewardRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case BETTING:
                makeBettingRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case BETTING_CANCLE:
                makeBettingCancleRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case BETTING_REWARD:
                makeBettingRewardRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case RACING, RACING_CANCEL, RACING_REWARD:
                makeRacingRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case EVENT:
                makeEventRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;

            case EVENT_CANCEL:
                makeEventCancelRollBack(member, pointChangeType, pointAmount, pointChangeReason, reasonId);
                break;
        }
    }

    private void makeScheduleEnterRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeScheduleExitRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeScheduleRewardRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeBettingRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeBettingCancleRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeBettingRewardRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeRacingRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeEventRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }

    private void makeEventCancelRollBack(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // TODO
    }
}
