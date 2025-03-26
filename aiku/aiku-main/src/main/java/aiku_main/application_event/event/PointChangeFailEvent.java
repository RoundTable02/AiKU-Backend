package aiku_main.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointChangeFailEvent {

    private Long memberId;
    private PointChangeType sign;
    private int pointAmount;

    private PointChangeReason reason;
    private Long reasonId;
}
