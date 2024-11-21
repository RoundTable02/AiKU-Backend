package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ArrivalAlarmMessage extends AlarmMessage {

    private long memberId;
    private long scheduleId;
    private String scheduleName;
    private LocalDateTime arrivalTime;
    private AlarmMemberInfo arriveMemberInfo;

    public ArrivalAlarmMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, long memberId, long scheduleId, String scheduleName, LocalDateTime arrivalTime, AlarmMemberInfo arriveMemberInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.arrivalTime = arrivalTime;
        this.arriveMemberInfo = arriveMemberInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleName", scheduleName);
        messageData.put("member", arriveMemberInfo.getAlarmMemberInfoJsonString());
        messageData.put("arrivalTime", arrivalTime);

        return messageData;
    }
}
