package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingTermMessage extends AlarmMessage{

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private Integer pointAmount;

    public RacingTermMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId, Integer pointAmount) {
        super(members, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
        this.pointAmount = pointAmount;
    }
}
