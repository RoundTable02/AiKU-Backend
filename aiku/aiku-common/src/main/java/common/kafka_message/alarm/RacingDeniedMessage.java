package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingDeniedMessage extends AlarmMessage {

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;

    public RacingDeniedMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId) {
        super(members, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
    }
}
