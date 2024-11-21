package common.kafka_message.alarm;

import common.domain.title.TitleCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TitleGrantedMessage extends AlarmMessage {
    private Long titleId;
    private String titleName;
    private String titleDescription;
    private TitleCode titleCode;

    public TitleGrantedMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, Long titleId, String titleName, String titleDescription, TitleCode titleCode) {
        super(alarmReceiverTokens, alarmMessageType);
        this.titleId = titleId;
        this.titleName = titleName;
        this.titleDescription = titleDescription;
        this.titleCode = titleCode;
    }


    @Override
    public Map<String, String> getMessage() {
        Map messageData = new HashMap();
        messageData.put("title", this.getAlarmMessageType().name());
        messageData.put("titleId", titleId);
        messageData.put("titleName", titleName);
        messageData.put("titleDescription", titleDescription);
        messageData.put("titleCode", titleCode.name());

        return messageData;
    }

    @Override
    public String getSimpleAlarmInfo() {
        return "칭호 : " + titleName + "을 획득하였습니다!";
    }
}
