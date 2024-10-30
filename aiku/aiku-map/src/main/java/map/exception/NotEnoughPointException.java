package map.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class NotEnoughPointException extends BaseException {

    public NotEnoughPointException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public NotEnoughPointException(StatusCode status){
        super(status, "NotEnoughPointException 멤버의 포인트가 부족합니다.");
    }

    public NotEnoughPointException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
