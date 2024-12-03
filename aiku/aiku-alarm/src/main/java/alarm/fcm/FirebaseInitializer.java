package alarm.fcm;

import alarm.exception.MessagingException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static common.response.status.BaseErrorCode.FAIL_TO_SEND_MESSAGE;

@Component
@Slf4j
public class FirebaseInitializer {
    @Value("firebase.config.type")
    private String type;
    @Value("firebase.config.project-id")
    private String projectId;
    @Value("firebase.config.private-key-id")
    private String privateKeyId;
    @Value("firebase.config.private-key")
    private String privateKey;
    @Value("firebase.config.client-email")
    private String clientEmail;
    @Value("firebase.config.client-id")
    private String clientId;
    @Value("firebase.config.auth-uri")
    private String authUri;
    @Value("firebase.config.token-uri")
    private String tokenUri;
    @Value("firebase.config.auth-provider-x509-cert-url")
    private String authProviderX509CertUrl;
    @Value("firebase.config.client-x509-cert-url")
    private String clientX509CertUrl;
    @Value("firebase.config.universe-domain")
    private String universeDomain;

    @PostConstruct
    public void initialize(){
        try {
            FirebaseConfig config = FirebaseConfig.builder()
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

            InputStream serviceAccount = config.toInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseInitializer app={}", app.getName());
        } catch (Exception e) {
            throw new MessagingException(FAIL_TO_SEND_MESSAGE);
        }
    }
}
