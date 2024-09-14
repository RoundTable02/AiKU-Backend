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

    @Embedded
    private ShopProductValue shopProduct;
}
