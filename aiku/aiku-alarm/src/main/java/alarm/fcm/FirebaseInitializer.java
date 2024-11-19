package alarm.fcm;

import alarm.exception.MessagingException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

import static common.response.status.BaseErrorCode.FAIL_TO_SEND_MESSAGE;

@Component
@Slf4j
public class FirebaseInitializer {
    @PostConstruct
    public void initialize(){
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/java/alarm/fcm/aiku2024-firebase-adminsdk-9fjfc-df87d6f024.json");

//            FileInputStream serviceAccount = new FileInputStream("/home/ubuntu/aiku_backend/AiKu_backend/aiku/src/main/java/alarm/fcm/aiku2024-firebase-adminsdk-9fjfc-df87d6f024.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseInitializer app={}", app.getName());
        } catch (IOException e) {
            throw new MessagingException(FAIL_TO_SEND_MESSAGE);
        }
    }
}
