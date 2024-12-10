package payment.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import common.domain.PaymentProduct;
import common.domain.PaymentProductType;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final String APPLICATION_NAME = "com.example.myapp";

    private AndroidPublisher initializePublisher() throws IOException {
        String accessToken = accessTokenProvider.getAccessToken();

        HttpRequestInitializer requestInitializer = request -> {
            request.setConnectTimeout(60000);
            request.setReadTimeout(60000);
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        };

        return new AndroidPublisher.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Transactional
    public ProductPurchase verifyProductPurchase(Long memberId, PaymentProductType type, String packageName, String productId, String purchaseToken) {
        try {
            validateDB(purchaseToken);

            PaymentProduct paymentProduct = getPaymentProduct(type);

            makePayment(memberId, purchaseToken, paymentProduct);

            AndroidPublisher publisher = initializePublisher();
            return publisher.purchases().products()
                    .get(packageName, productId, purchaseToken)
                    .execute();
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException: " + e.getDetails());
            throw new RuntimeException("Failed to verify purchase", e);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            throw new RuntimeException("Error initializing Google Play Publisher", e);
        }
    }

    @Transactional
    public void consumeProduct(PaymentProductType type, String packageName, String productId, String purchaseToken) {
        try {
            AndroidPublisher publisher = initializePublisher();
            publisher.purchases().products()
                    .consume(packageName, productId, purchaseToken)
                    .execute();
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException: " + e.getDetails());
            throw new RuntimeException("Failed to consume purchase", e);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            throw new RuntimeException("Error consuming purchase", e);
        }

        PaymentProduct paymentProduct = getPaymentProduct(type);
        paymentProduct.acceptPayment(purchaseToken);

        // 아쿠 증가 이벤트

        // 아쿠 증가 실패 시 환불

        // 충전 완료 알림
    }

    private PaymentProduct getPaymentProduct(PaymentProductType type) {
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

    private void validateDB(String purchaseToken) {
        // DB 내 purchaseToken 중복되는지 확인, true면 유일 보장
        if (paymentProductRepository.existsByPurchaseToken(purchaseToken)) {
            throw new PaymentException();
        }
    }

}
