package payment.util;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@NoArgsConstructor
@Component
public class AccessTokenProvider {
    public String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("path-to-service-account.json"))
                .createScoped("https://www.googleapis.com/auth/androidpublisher");
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
