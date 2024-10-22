package map.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class MemberNotInRacingException extends BaseException {

    public MemberNotInRacingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public MemberNotInRacingException(StatusCode status){
        super(status, "RacingException 서버 내부 오류입니다.");
    }

    public MemberNotInRacingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
