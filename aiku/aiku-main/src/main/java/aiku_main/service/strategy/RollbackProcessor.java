package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import common.domain.value_reference.MemberValue;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RollbackProcessor {

    private final Map<PointChangeReason, RollbackStrategy> strategies;

    public RollbackProcessor(List<RollbackStrategy> strategyList) {
        strategies = new HashMap<>();

        for (RollbackStrategy strategy : strategyList) {
            PointChangeReasonMapping mapping = strategy.getClass().getAnnotation(PointChangeReasonMapping.class);
            if (mapping != null) {
                for (PointChangeReason reason : mapping.value()) {
                    strategies.put(reason, strategy);
                }
            }
        }
    }

    public void process(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        RollbackStrategy strategy = strategies.get(pointChangeReason);
        if (strategy != null) {
            strategy.execute(memberId, pointChangeType, pointAmount, pointChangeReason, reasonId);
        } else {
            throw new IllegalArgumentException("Unsupported PointChangeReason: " + pointChangeReason);
        }
    }

}
