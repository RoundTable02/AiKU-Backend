package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class BettingException extends BaseException {

    public BettingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public BettingException(StatusCode status){
        super(status, "BettingException 서버 내부 오류입니다.");
    }

    public BettingException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
