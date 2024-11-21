package common.kafka_message.alarm;

import common.domain.Location;
import common.domain.schedule.Schedule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ScheduleAlarmMessage extends AlarmMessage{

    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime scheduleTime;
    private Location location;

    public ScheduleAlarmMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, LocalDateTime scheduleTime, Location location) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("scheduleTime", scheduleTime);
        messageData.put("location", getLocationJson());

        return messageData;
    }

    private String getLocationJson() {
        return "{" +
                "locationName" + ":" + location.getLocationName() + "," +
                "latitude" + ":" + location.getLatitude() + "," +
                "longitude" + ":" + location.getLongitude() + "," +
                "}";
    }
}
