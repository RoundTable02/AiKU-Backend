package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LocationAlarmMessage extends AlarmMessage {

    private long scheduleId;
    private AlarmMemberInfo memberInfo;
    private double latitude;
    private double longitude;

    public LocationAlarmMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, long scheduleId, AlarmMemberInfo memberInfo, double latitude, double longitude) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.memberInfo = memberInfo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("member", memberInfo.getAlarmMemberInfoJsonString());
        messageData.put("latitude", latitude);
        messageData.put("longitude", longitude);

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        // 실시간 좌표는 저장 x
        return null;
    }
}
