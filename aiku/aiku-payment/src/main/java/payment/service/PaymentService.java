package payment.service;

import common.domain.Payment;
import common.domain.PaymentProduct;
import common.domain.PaymentProductType;
import common.domain.member.Member;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import payment.controller.dto.PurchaseVerificationResponse;
import payment.exception.MemberNotFoundException;
import payment.exception.PaymentException;
import payment.repository.MemberRepository;
import payment.repository.PaymentProductRepository;
import payment.util.AccessTokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {

    private final AccessTokenProvider accessTokenProvider;
    private final PaymentProductRepository paymentProductRepository;
    private final MemberRepository memberRepository;
    private static final String GOOGLE_API_URL = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/products/{productId}/tokens/{token}";

    @Transactional
    public Long purchase(Long memberId, String packageName, String productId, String purchaseToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 영수증 검증
        String url = GOOGLE_API_URL.replace("{packageName}", packageName)
                .replace("{productId}", productId)
                .replace("{token}", purchaseToken);

        String accessToken;
        try {
            accessToken = accessTokenProvider.getAccessToken();
        } catch (IOException e) {
            throw new PaymentException(BaseErrorCode.PAYMENT_EXCEPTION);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PurchaseVerificationResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, PurchaseVerificationResponse.class
        );

        PurchaseVerificationResponse responseBody = response.getBody();

        PaymentProduct paymentProduct = getPaymentProduct(responseBody);

        // DB에 기록
        makePayment(memberId, purchaseToken, paymentProduct);

        if (responseBody.getPurchaseState() == 0) {
            // 소모 처리
            String consumeUrl = url + ":consume";
            restTemplate.exchange(consumeUrl, HttpMethod.POST, entity, Void.class);

            // DB 내 검증
            validateDB(purchaseToken);

            // 구매 처리
            paymentProduct.acceptPayment(purchaseToken);
        }

        return memberId;

        // 아쿠 증가 이벤트

        // 아쿠 증가 실패 시 환불

        // 충전 완료 알림

    }

    private PaymentProduct getPaymentProduct(PurchaseVerificationResponse responseBody) {
        PaymentProductType type = PaymentProductType.valueOf(responseBody.getDeveloperPayload());
        PaymentProduct paymentProduct = paymentProductRepository.findByPaymentProductType(type)
                .orElseThrow(PaymentException::new);
        return paymentProduct;
    }

    private void makePayment(Long memberId, String purchaseToken, PaymentProduct paymentProduct) {
        int price = paymentProduct.getPaymentProductType().getPrice();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        paymentProduct.makePayment(member, price, purchaseToken);
    }

    private boolean validateDB(String purchaseToken) {
        // DB 내 purchaseToken 중복되는지 확인, true면 유일 보장
        return !paymentProductRepository.existsByPurchaseToken(purchaseToken);
    }

}
