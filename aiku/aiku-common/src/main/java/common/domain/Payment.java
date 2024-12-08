package common.domain;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseTime{

    @Column(name = "paymentId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "paymentProductId")
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentProduct paymentProduct;

    @Embedded
    private MemberValue memberValue;

    // 실제 지불한 값
    private int price;

    private String purchaseToken;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.AWAIT;

    public void updatePayment(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public Payment(PaymentProduct paymentProduct, MemberValue memberValue, int price, String purchaseToken) {
        this.paymentProduct = paymentProduct;
        this.memberValue = memberValue;
        this.price = price;
        this.purchaseToken = purchaseToken;
    }
}
