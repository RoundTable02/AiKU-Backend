package common.domain.log;

import common.domain.value_reference.ShopProductValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@DiscriminatorValue(value = "SHOP")
@Entity
public class ShopPointLog {

    @Column(name = "shopPointLogId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ShopProductValue shopProduct;
}
