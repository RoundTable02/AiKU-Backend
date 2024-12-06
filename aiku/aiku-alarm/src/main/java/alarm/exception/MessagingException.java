package alarm.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class MessagingException extends BaseException {

    public MessagingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public MessagingException(StatusCode status){
        super(status, "MessagingException 서버 내부 오류입니다.");
    }

    public MessagingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
