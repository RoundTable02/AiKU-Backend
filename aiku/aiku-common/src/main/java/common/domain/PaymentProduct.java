package common.domain;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PaymentProduct extends BaseTime{

    @Column(name = "paymentProductId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PaymentProductType paymentProductType;

    @OneToMany(mappedBy = "paymentProduct", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    private String productId; // 인앱결제 내부 productId

    @Builder
    public PaymentProduct(PaymentProductType paymentProductType, String productId) {
        this.paymentProductType = paymentProductType;
        this.productId = productId;
    }

    public void makePayment(Member member, int price, String purchaseToken) {
        Payment payment = new Payment(this, new MemberValue(member), price, purchaseToken);
        this.payments.add(payment);
    }

    public boolean acceptPayment(Payment payment) {
        if (payment.getPaymentProduct().getId().equals(id)) {
            payment.updatePayment(PaymentStatus.ACCEPT);
            return true;
        }
        return false;
    }

    public boolean denyPayment(Payment payment) {
        if (payment.getPaymentProduct().getId().equals(id)) {
            payment.updatePayment(PaymentStatus.DENIED);
            return true;
        }
        return false;
    }

    public boolean invalidatePayment(Payment payment) {
        if (payment.getPaymentProduct().getId().equals(id)) {
            payment.updatePayment(PaymentStatus.INVALID);
            return true;
        }
        return false;
    }
}
