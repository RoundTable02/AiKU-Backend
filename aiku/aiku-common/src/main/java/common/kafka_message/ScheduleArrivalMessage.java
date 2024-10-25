package common.kafka_message;

import common.domain.value_reference.MemberValue;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleArrivalMessage {

    private Long scheduleId;
    private Long memberId;
    private LocalDateTime arrivalTime;

    public ScheduleArrivalMessage(Long scheduleId, MemberValue member, LocalDateTime arrivalTime) {
        this.scheduleId = scheduleId;
        this.memberId = member.getId();
        this.arrivalTime = arrivalTime;
    }
}
