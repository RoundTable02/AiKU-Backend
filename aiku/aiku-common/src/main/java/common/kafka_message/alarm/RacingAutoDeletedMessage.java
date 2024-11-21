package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingAutoDeletedMessage extends AlarmMessage {

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private Integer point;
    private AlarmMemberInfo secondRacerInfo;

    public RacingAutoDeletedMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId, Integer point, AlarmMemberInfo secondRacerInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
        this.point = point;
        this.secondRacerInfo = secondRacerInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("racingId", racingId);
        messageData.put("point", point);
        messageData.put("member", secondRacerInfo.getAlarmMemberInfoJsonString());

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        return "약속 : " + scheduleName + "에서 멤버 " + secondRacerInfo.getNickname() +
                "에게 신청한 레이싱이 거절되었습니다.";
    }
}
