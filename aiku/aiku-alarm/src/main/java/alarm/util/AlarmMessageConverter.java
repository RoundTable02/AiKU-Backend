package alarm.util;

import common.kafka_message.alarm.AlarmMessage;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@NoArgsConstructor
@Component
public class AlarmMessageConverter {

    // Firebase 알림 전달 용 메시지 생성
    public Map<String, String> getMessage(AlarmMessage alarmMessage) {
        return null;
    }

    // DB 알림 저장 용
    public String getSimpleAlarmInfo(AlarmMessage alarmMessage) {
        return null;
    }


}
