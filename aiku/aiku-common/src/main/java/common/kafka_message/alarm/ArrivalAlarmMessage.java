package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ArrivalAlarmMessage extends AlarmMessage {

    private long memberId;
    private long scheduleId;
    private String scheduleName;
    private LocalDateTime arrivalTime;

    public ArrivalAlarmMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, long memberId, long scheduleId, String scheduleName, LocalDateTime arrivalTime) {
        super(members, alarmMessageType);
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.arrivalTime = arrivalTime;
    }
}
