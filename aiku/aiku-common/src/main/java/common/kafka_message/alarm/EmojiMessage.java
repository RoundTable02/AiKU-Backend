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
    private AlarmMemberInfo senderInfo;
    private AlarmMemberInfo receiverInfo;

    public EmojiMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, long scheduleId, String scheduleName, String emojiType, AlarmMemberInfo senderInfo, AlarmMemberInfo receiverInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.emojiType = emojiType;
        this.senderInfo = senderInfo;
        this.receiverInfo = receiverInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("sender", senderInfo.getAlarmMemberInfoJsonString());
        messageData.put("receiver", receiverInfo.getAlarmMemberInfoJsonString());

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        return "약속 : " + scheduleName + "에서 멤버 " + senderInfo.getNickname() + "가 " + emojiType + " 이모지를 전달했습니다.";
    }
}
