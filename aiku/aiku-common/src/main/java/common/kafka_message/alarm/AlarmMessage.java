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
public class AlarmMessage {

    private List<String> alarmReceiverTokens;
    private AlarmMessageType alarmMessageType;

}
