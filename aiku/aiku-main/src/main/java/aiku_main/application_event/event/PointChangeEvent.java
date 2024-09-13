package aiku_main.application_event.event;

import common.domain.value_reference.MemberValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointChangeEvent {
    private MemberValue member;
    private PointChangeType sign;
    private int pointAmount;

    private PointChangeReason reason;
    private Long reasonId;
}
