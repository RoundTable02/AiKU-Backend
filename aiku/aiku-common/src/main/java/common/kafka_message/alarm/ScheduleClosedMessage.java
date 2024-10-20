package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ScheduleClosedMessage extends AlarmMessage {

    private long scheduleId;
    private String scheduleName;
    private String locationName;
    private LocalDateTime scheduleTime;

    public ScheduleClosedMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, long scheduleId, String scheduleName, String locationName, LocalDateTime scheduleTime) {
        super(members, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.locationName = locationName;
        this.scheduleTime = scheduleTime;
    }
}
