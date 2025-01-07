package common.kafka_message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingPointChangedFailedMessage {

    private Long memberId;
    private PointChangedType pointChangedType;
    private Integer pointAmount;

    private PointChangeReason reason;
    private Long reasonId;

    public RacingPointChangedFailedMessage(Long memberId, PointChangedType pointChangedType, Integer pointAmount, PointChangeReason reason, Long reasonId) {
        this.memberId = memberId;
        this.pointChangedType = pointChangedType;
        this.pointAmount = pointAmount;
        this.reason = reason;
        this.reasonId = reasonId;
    }
}
