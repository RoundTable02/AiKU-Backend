package common.domain;

import common.domain.BaseTime;
import common.kafka_message.alarm.AlarmMessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class MemberMessage extends BaseTime {

    @Column(name = "memberMessageId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AlarmMessageType messageType;

    private Long memberId;

    private String alarmMessageInfo;

    public MemberMessage(AlarmMessageType messageType, Long memberId, String alarmMessageInfo) {
        this.messageType = messageType;
        this.memberId = memberId;
        this.alarmMessageInfo = alarmMessageInfo;
    }

}
