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
public class PointChangedMessage extends AlarmMessage{

    private Long memberId;
    private PointChangedType pointChangedType;
    private Integer pointAmount;

    public PointChangedMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, Long memberId, PointChangedType pointChangedType, Integer pointAmount) {
        super(members, alarmMessageType);
        this.memberId = memberId;
        this.pointChangedType = pointChangedType;
        this.pointAmount = pointAmount;
    }
}
