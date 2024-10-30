package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class NoSuchTermException extends BaseException {

    public NoSuchTermException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public NoSuchTermException(StatusCode status){
        super(status, "TermException 서버 내부 오류입니다.");
    }

    public NoSuchTermException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
