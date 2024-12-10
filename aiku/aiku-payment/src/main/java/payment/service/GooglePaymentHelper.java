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
import lombok.experimental.Helper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import payment.exception.MemberNotFoundException;
import payment.exception.PaymentException;
import payment.repository.MemberRepository;
import payment.repository.PaymentProductRepository;
import payment.util.AccessTokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class GooglePaymentHelper {

    private final AccessTokenProvider accessTokenProvider;

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

    public ProductPurchase verifyProductPurchase(String packageName, String productId, String purchaseToken) {
        try {
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

    public void consumeProduct(String packageName, String productId, String purchaseToken) {
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
    }

}
