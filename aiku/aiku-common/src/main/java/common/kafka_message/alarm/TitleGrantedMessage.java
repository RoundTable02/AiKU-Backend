package common.kafka_message.alarm;

import common.domain.title.TitleCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TitleGrantedMessage extends AlarmMessage {
    private Long titleId;
    private String titleName;
    private String titleDescription;
    private TitleCode titleCode;

    public TitleGrantedMessage(List<AlarmMemberInfo> members, AlarmMessageType alarmMessageType, Long titleId, String titleName, String titleDescription, TitleCode titleCode) {
        super(members, alarmMessageType);
        this.titleId = titleId;
        this.titleName = titleName;
        this.titleDescription = titleDescription;
        this.titleCode = titleCode;
    }
}
