package alarm.controller.dto;

import common.domain.MemberMessage;
import common.kafka_message.alarm.AlarmMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberMessageDto {

    private AlarmMessageType messageType;
    private String alarmMessageInfo;

    public static MemberMessageDto toDto(MemberMessage message) {
        return new MemberMessageDto(message.getMessageType(), message.getAlarmMessageInfo());
    }
}
