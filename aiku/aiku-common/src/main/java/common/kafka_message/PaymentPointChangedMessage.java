package common.kafka_message;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentPointChangedMessage {

    private MemberValue member;
    private PointChangedType pointChangedType;
    private Integer pointAmount;
    private String purchaseToken;

    public PaymentPointChangedMessage(Member member, PointChangedType pointChangedType, Integer pointAmount, String purchaseToken) {
        this.member = new MemberValue(member);
        this.pointChangedType = pointChangedType;
        this.pointAmount = pointAmount;
        this.purchaseToken = purchaseToken;
    }
}
