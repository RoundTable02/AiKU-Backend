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

    @Column(name = "paymentLogId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
