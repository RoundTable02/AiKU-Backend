package payment.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseVerificationResponse {
    private int purchaseState;      // 구매 상태 (0: 완료, 1: 취소 등)
    private int consumptionState;   // 소비 상태 (0: 소비되지 않음, 1: 소비됨)
    private long purchaseTimeMillis; // 구매 시간 (밀리초)
    private String orderId;         // 주문 ID
    private String developerPayload; // 개발자 지정 추가 데이터
}
