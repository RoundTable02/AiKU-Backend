package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class TermException extends BaseException {

    public TermException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public TermException(StatusCode status){
        super(status, "TermException 서버 내부 오류입니다.");
    }

    public TermException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
