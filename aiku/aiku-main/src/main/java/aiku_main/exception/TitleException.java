package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class TitleException extends BaseException {

    public TitleException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public TitleException(StatusCode status){
        super(status, "TitleException 서버 내부 오류입니다.");
    }

    public TitleException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
