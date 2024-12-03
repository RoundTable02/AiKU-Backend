package alarm.fcm;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import alarm.exception.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static common.response.status.BaseErrorCode.FAIL_TO_SEND_MESSAGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageSender {

    public void sendMessage(Map<String, String> messageDataMap, List<String> receiverTokens) {
        receiverTokens.removeAll(Collections.singletonList(null));

        if (receiverTokens.size() == 1) {
            sendMessageToUser(messageDataMap, receiverTokens.get(0));
        } else if (!receiverTokens.isEmpty()) {
            sendMessageToUsers(messageDataMap, receiverTokens);
        }
    }

    private void sendMessageToUsers(Map<String, String> messageDataMap, List<String> receiverTokens) {
        log.info("Firebase sendMessageToUsers");
        MulticastMessage message = MulticastMessage.builder()
                .putAllData(messageDataMap)
                .addAllTokens(receiverTokens)
                .build();

        ApiFuture<BatchResponse> future = FirebaseMessaging.getInstance().sendEachForMulticastAsync(message);

        future.addListener(() -> {
            try {
                BatchResponse batchResponse = future.get();
                log.info("Successfully sent messages: {}" + batchResponse.getSuccessCount());
                log.info("Failed messages: {}" + batchResponse.getFailureCount());

                batchResponse.getResponses().forEach(response -> {
                    if (!response.isSuccessful()) {
                        log.info("Error sending message: " + response.getException().getMessage());
                    }
                });
            } catch (Exception e) {
                throw new MessagingException(FAIL_TO_SEND_MESSAGE);
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void sendMessageToUser(Map<String, String> messageDataMap, String receiverToken) {
        log.info("Firebase sendMessageToUser");
        Message message = Message.builder()
                .putAllData(messageDataMap)
                .setToken(receiverToken)
                .build();

        ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(message);

        future.addListener(() -> {
            try {
                String response = future.get();
                log.info("Message sent successfully: {}", response);
            } catch (Exception e) {
                throw new MessagingException(FAIL_TO_SEND_MESSAGE);
            }
        }, Executors.newSingleThreadExecutor());
    }
}
