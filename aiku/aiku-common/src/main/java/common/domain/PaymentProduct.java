package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PaymentProduct extends BaseTime{

    @Column(name = "paymentProductId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;
    private int pointAmount;
}
