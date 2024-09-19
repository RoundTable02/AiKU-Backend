package common.domain.value_reference;

import common.domain.ShopProduct;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class ShopProductValue {

    @Column(name = "shopProductId")
    private Long id;

    public ShopProductValue(ShopProduct shopProduct) {
        this.id = shopProduct.getId();
    }
}
