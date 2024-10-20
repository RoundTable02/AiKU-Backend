package common.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlarmMessage {

    private Long userId; // 푸시 알람 대상의 고유 아이디
    private List<String> firebaseTokens; // 푸시 알람 대상의 파이어베이스 토큰
    private AlarmMessageType alarmMessageType;
}
