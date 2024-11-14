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
public class ShopPointLog extends PointLog{

    @Embedded
    private ShopProductValue shopProduct;

    public ShopPointLog(Long memberId, int pointAmount, String description, PointLogStatus pointLogStatus, ShopProductValue shopProduct) {
        super(memberId, pointAmount, description, pointLogStatus);
        this.shopProduct = shopProduct;
    }
}
