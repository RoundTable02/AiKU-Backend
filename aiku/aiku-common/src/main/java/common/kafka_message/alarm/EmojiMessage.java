package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EmojiMessage extends AlarmMessage {

    // List에 Sender, Receiver 순서로 담긴다
    private long scheduleId;
    private String scheduleName;
    private String emojiType;

    public EmojiMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, long scheduleId, String scheduleName, String emojiType) {
        super(members, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.emojiType = emojiType;
    }
}
