package common.kafka_message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentPointChangedMessage {

    private Long memberId;
    private PointChangedType pointChangedType;
    private Integer pointAmount;
    private String purchaseToken;

    public PaymentPointChangedMessage(Long memberId, PointChangedType pointChangedType, Integer pointAmount, String purchaseToken) {
        this.memberId = memberId;
        this.pointChangedType = pointChangedType;
        this.pointAmount = pointAmount;
        this.purchaseToken = purchaseToken;
    }
}
