package common.kafka_message.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlarmMessage {

    private List<AlarmMemberInfo> members;
    private AlarmMessageType alarmMessageType;
}
