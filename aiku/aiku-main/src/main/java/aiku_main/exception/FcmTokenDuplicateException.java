package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class FcmTokenDuplicateException extends BaseException {

    public FcmTokenDuplicateException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public FcmTokenDuplicateException(StatusCode status){
        super(status, "토큰 중복 에러입니다.");
    }

    public FcmTokenDuplicateException() {
        this(BaseErrorCode.DUPLICATED_FCM_TOKEN);
    }
}
