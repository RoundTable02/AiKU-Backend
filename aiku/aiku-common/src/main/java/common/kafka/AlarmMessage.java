package common.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessage {

    private Long userId; // 푸시 알람 대상의 고유 아이디
    private Long firebaseToken; // 푸시 알람 대상의 파이어베이스 토큰
    private Long associatedId; // 푸시 알람의 원인이 되는 도메인의 고유 아이디 (alarmMessageType로 구분)
    private AlarmMessageType alarmMessageType;
}
