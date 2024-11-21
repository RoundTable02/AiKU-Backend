package common.kafka_message.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlarmMessage {

    private List<String> alarmReceiverTokens;
    private AlarmMessageType alarmMessageType;

    // Firebase 알림 전달 용 메시지 생성
    public abstract Map<String, String> getMessage();

    // DB 알림 저장 용
    public abstract String getSimpleAlarmInfo();

}
