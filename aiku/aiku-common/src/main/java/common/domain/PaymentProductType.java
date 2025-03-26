package common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentProductType {

    PRODUCT01(350, 5000),
    PRODUCT02(750, 10000),
    PRODUCT03(2200, 30000),
    PRODUCT04(3700, 50000);

    private int point;
    private int price;
}
