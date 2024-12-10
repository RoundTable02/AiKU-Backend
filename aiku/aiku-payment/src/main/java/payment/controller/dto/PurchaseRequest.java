package payment.controller.dto;

import common.domain.PaymentProductType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    @NotNull
    private PaymentProductType type;      // 앱의 패키지 이름
    @NotNull
    private String packageName;      // 앱의 패키지 이름
    @NotNull
    private String productId;        // 상품 ID
    @NotNull
    private String purchaseToken;    // 구매 토큰
}
