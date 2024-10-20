package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class NoFcmTokenException extends BaseException {

    public NoFcmTokenException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public NoFcmTokenException(StatusCode status){
        super(status, "토큰 중복 에러입니다.");
    }

    public NoFcmTokenException() {
        this(BaseErrorCode.NO_FCM_TOKEN);
    }
}
