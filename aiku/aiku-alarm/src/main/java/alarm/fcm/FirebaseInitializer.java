package alarm.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FirebaseInitializer {

    @PostConstruct
    public void initialize(){
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("aiku2024-firebase-adminsdk-9fjfc-df87d6f024.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseInitializer app={}", app.getName());
        } catch (IOException e) {
            throw new RuntimeException("FirebaseInitializer 오류", e);
        }
    }
}
