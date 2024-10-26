package map.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class NoSuchRacingException extends BaseException {

    public NoSuchRacingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public NoSuchRacingException(StatusCode status){
        super(status, "RacingException 서버 내부 오류입니다.");
    }

    public NoSuchRacingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
