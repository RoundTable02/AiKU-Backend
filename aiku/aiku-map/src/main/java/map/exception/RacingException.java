package map.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class RacingException extends BaseException {

    public RacingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public RacingException(StatusCode status){
        super(status, "RacingException 서버 내부 오류입니다.");
    }

    public RacingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
