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

// AlarmMessageType = SCHEDULE_ENTER, SCHEDULE_EXIT
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ScheduleMemberAlarmMessage extends AlarmMessage{

    private AlarmMemberInfo sourceMember;
    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime scheduleTime;
    private Location location;

    public ScheduleMemberAlarmMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, AlarmMemberInfo sourceMember, Long scheduleId, String scheduleName, LocalDateTime scheduleTime, Location location) {
        super(alarmReceiverTokens, alarmMessageType);
        this.sourceMember = sourceMember;
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
        messageData.put("sourceMember", sourceMember.getAlarmMemberInfoJsonString());
        messageData.put("location", getLocationJson());

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        // SCHEDULE_ADD, SCHEDULE_ENTER, SCHEDULE_EXIT, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
        String statement = switch (this.getAlarmMessageType()) {
            case SCHEDULE_ADD -> " 가 추가되었습니다.";
            case SCHEDULE_ENTER -> " 에 멤버가 입장하였습니다.";
            case SCHEDULE_EXIT -> " 에서 퇴장하였습니다.";
            case SCHEDULE_UPDATE -> " 이 업데이트 되었습니다.";
            case SCHEDULE_OWNER -> " 의 스케줄 장이 변경되었습니다.";
            case SCHEDULE_OPEN -> " 맵이 생성되었습니다!";
            case SCHEDULE_AUTO_CLOSE -> " 이 자동 종료되었습니다.";
            default -> "";
        };

        return "약속 : " + scheduleName + statement;
    }

    private String getLocationJson() {
        return "{" +
                "locationName" + ":" + location.getLocationName() + "," +
                "latitude" + ":" + location.getLatitude() + "," +
                "longitude" + ":" + location.getLongitude() + "," +
                "}";
    }
}
