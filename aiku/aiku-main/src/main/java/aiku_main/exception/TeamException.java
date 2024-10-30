package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class TeamException extends BaseException {

    public TeamException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public TeamException(StatusCode status){
        super(status, "TeamException 서버 내부 오류입니다.");
    }

    public TeamException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
