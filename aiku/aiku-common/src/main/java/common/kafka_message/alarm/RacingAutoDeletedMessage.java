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
public class RacingAutoDeletedMessage extends AlarmMessage {

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private Integer point;
    private AlarmMemberInfo memberInfo;

    public RacingAutoDeletedMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId, Integer point, AlarmMemberInfo memberInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
        this.point = point;
        this.memberInfo = memberInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("racingId", racingId);
        messageData.put("point", point);
        messageData.put("member", memberInfo.getAlarmMemberInfoJsonString());

        return messageData;
    }
}
