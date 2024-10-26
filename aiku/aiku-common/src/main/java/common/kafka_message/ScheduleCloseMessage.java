package common.kafka_message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ScheduleCloseMessage {

    private Long scheduleId;
    private LocalDateTime scheduleCloseTime;
}
