package aiku_main.application_event.event;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import lombok.Getter;

@Getter
public class PaymentPointChangeEvent {

    private MemberValue member;
    private PointChangeType sign;
    private int pointAmount;

    private PointChangeReason reason;

    private String purchaseToken;

    public PaymentPointChangeEvent(MemberValue member, PointChangeType sign, int pointAmount, PointChangeReason reason, String purchaseToken) {
        this.member = member;
        this.sign = sign;
        this.pointAmount = pointAmount;
        this.reason = reason;
        this.purchaseToken = purchaseToken;
    }
}
