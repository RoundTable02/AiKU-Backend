package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// AlarmMessageType = SCHEDULE_ADD, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RacingTermMessage extends AlarmMessage{

    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private Integer point;
    private AlarmMemberInfo winnerRacerInfo;
    private AlarmMemberInfo loserRacerInfo;

    public RacingTermMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long scheduleId, String scheduleName, Long racingId, Integer point, AlarmMemberInfo winnerRacerInfo, AlarmMemberInfo loserRacerInfo) {
        super(alarmReceiverTokens, alarmMessageType);
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.racingId = racingId;
        this.point = point;
        this.winnerRacerInfo = winnerRacerInfo;
        this.loserRacerInfo = loserRacerInfo;
    }

    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("scheduleId", scheduleId);
        messageData.put("scheduleName", scheduleName);
        messageData.put("racingId", racingId);
        messageData.put("point", point);
        messageData.put("winnerRacer", winnerRacerInfo.getAlarmMemberInfoJsonString());
        messageData.put("loserRacer", loserRacerInfo.getAlarmMemberInfoJsonString());

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        return "약속 : " + scheduleName + "에서 멤버 " + winnerRacerInfo.getNickname() + "와 " + loserRacerInfo.getNickname()
                + "의 레이싱이 "+ winnerRacerInfo.getNickname() + "의 승리로 종료되었습니다.";
    }
}
