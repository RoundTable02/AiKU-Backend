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

    private List<AlarmMemberInfo> members;
    private AlarmMessageType alarmMessageType;

    public abstract Map<String, String> getMessage();

    public final List<String> getAlarmMembersFcmTokens() {
        return members.stream()
                .map(AlarmMemberInfo::getFirebaseToken)
                .collect(Collectors.toList());
    }

    public final Optional<String> getMemberInfoJsonString(Long memberId) {
        return members.stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .map(AlarmMemberInfo::getAlarmMemberInfoJsonString)
                .findFirst();
    }
}
