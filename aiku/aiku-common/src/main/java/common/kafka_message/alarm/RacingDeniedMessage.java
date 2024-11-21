package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingDeniedMessage extends AlarmMessage {

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private AlarmMemberInfo memberInfo;

    public RacingDeniedMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId, AlarmMemberInfo memberInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
        this.memberInfo = memberInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("racingId", racingId);
        messageData.put("member", memberInfo.getAlarmMemberInfoJsonString());

        return messageData;
    }
}
