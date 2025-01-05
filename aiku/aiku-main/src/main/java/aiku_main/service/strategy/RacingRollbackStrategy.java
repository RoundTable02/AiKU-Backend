package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.value_reference.MemberValue;
import org.springframework.stereotype.Component;

@Component
@PointChangeReasonMapping({PointChangeReason.RACING, PointChangeReason.RACING_REWARD, PointChangeReason.RACING_CANCEL})
public class RacingRollbackStrategy implements RollbackStrategy {

    @Override
    public void execute(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        System.out.println("Processing RACING rollback");
    }
}
