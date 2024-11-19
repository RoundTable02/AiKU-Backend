package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("sender", racingId);

        return messageData;
    }
}
