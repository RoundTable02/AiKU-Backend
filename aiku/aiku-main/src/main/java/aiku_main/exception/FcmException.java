package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class FcmException extends BaseException {

    public FcmException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public FcmException(StatusCode status){
        super(status, "토큰 중복 에러입니다.");
    }

    public FcmException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
