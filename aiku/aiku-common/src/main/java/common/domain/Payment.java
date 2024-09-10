package common.domain;

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

    private int price;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;
}
