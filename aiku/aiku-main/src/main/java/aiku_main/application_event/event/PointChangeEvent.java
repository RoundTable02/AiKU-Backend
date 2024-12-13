package aiku_main.application_event.event;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import lombok.Getter;

@Getter
public class PointChangeEvent {

    private MemberValue member;
    private PointChangeType sign;
    private int pointAmount;

    private PointChangeReason reason;
    private Long reasonId;

    public PointChangeEvent(Member member, PointChangeType sign, int pointAmount, PointChangeReason reason, Long reasonId) {
        this.member = new MemberValue(member);
        this.sign = sign;
        this.pointAmount = pointAmount;
        this.reason = reason;
        this.reasonId = reasonId;
    }

    public PointChangeEvent(MemberValue member, PointChangeType sign, int pointAmount, PointChangeReason reason, Long reasonId) {
        this.member = member;
        this.sign = sign;
        this.pointAmount = pointAmount;
        this.reason = reason;
        this.reasonId = reasonId;
    }
}
