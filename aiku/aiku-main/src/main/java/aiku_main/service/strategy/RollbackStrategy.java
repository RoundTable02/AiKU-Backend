package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.value_reference.MemberValue;

public interface RollbackStrategy {

    void execute(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId);
}
