package common.domain.log;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "PAYMENT")
@Entity
public class PaymentLog extends PointLog{

    public PaymentLog(Long memberId, int pointAmount, String description, PointLogStatus pointLogStatus) {
        super(memberId, pointAmount, description, pointLogStatus);
    }
}
