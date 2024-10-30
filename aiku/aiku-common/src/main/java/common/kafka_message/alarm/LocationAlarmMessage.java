package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LocationAlarmMessage extends AlarmMessage {

    private long memberId;
    private long scheduleId;
    private double latitude;
    private double longitude;

    public LocationAlarmMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, long memberId, long scheduleId, double latitude, double longitude) {
        super(members, alarmMessageType);
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
