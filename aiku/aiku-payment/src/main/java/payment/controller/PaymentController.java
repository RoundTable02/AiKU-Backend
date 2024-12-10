package payment.controller;

import com.google.api.services.androidpublisher.model.ProductPurchase;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import payment.controller.dto.PurchaseRequest;
import payment.controller.dto.PurchaseVerificationResponse;
import payment.exception.PaymentException;
import payment.service.PaymentService;
import payment.util.AccessTokenProvider;

import java.io.IOException;

@RequestMapping("/payment")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public BaseResponse<BaseResultDto> verifyPurchase(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                      @RequestBody PurchaseRequest request) {

        Long purchase = paymentService.purchase(memberId,
                request.getPackageName(),
                request.getProductId(),
                request.getPurchaseToken());

        return BaseResponse.getSimpleRes(purchase);
    }

    @PostMapping("/verify-and-consume")
    public ResponseEntity<String> verifyAndConsume(@RequestBody PurchaseRequest request) {
        // 1. 구매 검증
        ProductPurchase purchase = paymentService.verifyProductPurchase(request.getPackageName(), request.getProductId(), request.getPurchaseToken());

        // 2. 구매 상태 확인
        if (purchase.getPurchaseState() == 0 && purchase.getConsumptionState() == 0) {
            // 3. 소비 처리
            paymentService.consumeProduct(request.getPackageName(), request.getProductId(), request.getPurchaseToken());
            return ResponseEntity.ok("Purchase verified and consumed successfully.");
        }

        return ResponseEntity.badRequest().body("Invalid or already consumed purchase.");
    }
}
