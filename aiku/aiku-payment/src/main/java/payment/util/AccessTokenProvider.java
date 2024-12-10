package payment.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import payment.exception.PaymentException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@NoArgsConstructor
@Component
public class AccessTokenProvider {

    @Value("${google.config.type}")
    private String type;
    @Value("${google.config.project-id}")
    private String projectId;
    @Value("${google.config.private-key-id}")
    private String privateKeyId;
    @Value("${google.config.private-key}")
    private String privateKey;
    @Value("${google.config.client-email}")
    private String clientEmail;
    @Value("${google.config.client-id}")
    private String clientId;
    @Value("${google.config.auth-uri}")
    private String authUri;
    @Value("${google.config.token-uri}")
    private String tokenUri;
    @Value("${google.config.auth-provider-x509-cert-url}")
    private String authProviderX509CertUrl;
    @Value("${google.config.client-x509-cert-url}")
    private String clientX509CertUrl;
    @Value("${google.config.universe-domain}")
    private String universeDomain;

    private GoogleCredentials getCredentials() {
        GoogleConfig config = GoogleConfig.builder()
                .type(type)
                .project_id(projectId)
                .private_key_id(privateKeyId)
                .private_key(privateKey)
                .client_email(clientEmail)
                .client_id(clientId)
                .auth_uri(authUri)
                .token_uri(tokenUri)
                .auth_provider_x509_cert_url(authProviderX509CertUrl)
                .client_x509_cert_url(clientX509CertUrl)
                .universe_domain(universeDomain)
                .build();

        try {
            return ServiceAccountCredentials.fromStream(config.toInputStream())
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"));
        } catch (Exception e) {
            throw new PaymentException();
        }
    }

    public String getAccessToken() throws IOException {
        GoogleCredentials credentials = getCredentials();
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
