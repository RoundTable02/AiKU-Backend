package common.kafka_message.alarm;

public enum AlarmMessageType {
    TEST, SCHEDULE_ADD, SCHEDULE_ENTER, SCHEDULE_EXIT, SCHEDULE_UPDATE, SCHEDULE_OWNER, SCHEDULE_OPEN, SCHEDULE_AUTO_CLOSE,
    MEMBER_REAL_TIME_LOCATION, MEMBER_ARRIVAL, SCHEDULE_MAP_CLOSE, EMOJI,
    ASK_RACING, RACING_AUTO_DELETED, RACING_DENIED, RACING_TERM, RACING_START,
    TITLE_GRANTED, PAYMENT_SUCCESS, PAYMENT_FAILED,
    POINT_ERROR
}
