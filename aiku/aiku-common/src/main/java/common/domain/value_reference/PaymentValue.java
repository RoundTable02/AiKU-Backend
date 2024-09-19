package common.domain.value_reference;

import common.domain.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class PaymentValue {

    @Column(name = "memberId")
    private Long id;

    public PaymentValue(Payment payment) {
        this.id = payment.getId();
    }
}
