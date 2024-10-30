package map.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class DuplicateRacingException extends BaseException {

    public DuplicateRacingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public DuplicateRacingException(StatusCode status){
        super(status, "RacingException 서버 내부 오류입니다.");
    }

    public DuplicateRacingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
