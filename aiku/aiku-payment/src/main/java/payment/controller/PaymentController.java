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
    public BaseResponse<BaseResultDto> verifyAndConsume(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                                   @RequestBody PurchaseRequest request) {
        Long dataMemberId = paymentService.verifyProductPurchase(
                memberId,
                request.getType(),
                request.getPurchaseToken()
        );

        return BaseResponse.getSimpleRes(dataMemberId);
    }
}
