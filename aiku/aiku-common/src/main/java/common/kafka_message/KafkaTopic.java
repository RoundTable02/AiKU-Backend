package common.kafka_message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopic {

    TEST("test"),
    ALARM("alarm"),
    POINT_CHANGE("point-change"),
    SCHEDULE_CLOSE("schedule-close"),
    SCHEDULE_AUTO_CLOSE("schedule-auto-close"),
    POINT_CHANGE_FAILURE("point-change-failure");

    private String name;
}
