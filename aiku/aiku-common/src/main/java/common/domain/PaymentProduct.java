package common.domain;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    public void makePayment(Member member, int price, String purchaseToken) {
        Payment payment = new Payment(this, new MemberValue(member), price, purchaseToken);
        this.payments.add(payment);
    }

    public void acceptPayment(String purchaseToken) {
        payments.stream()
                .filter(payment -> payment.getPurchaseToken().equals(purchaseToken))
                .findFirst()
                .ifPresent(payment -> payment.updatePayment(PaymentStatus.ACCEPT));
    }
}
