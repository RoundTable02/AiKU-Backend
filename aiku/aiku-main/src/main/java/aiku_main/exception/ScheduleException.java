package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class ScheduleException extends BaseException {

    public ScheduleException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public ScheduleException(StatusCode status){
        super(status, "ScheduleException 서버 내부 오류입니다.");
    }

    public ScheduleException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
