package payment.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class PaymentException extends BaseException {

    public PaymentException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public PaymentException(StatusCode status){
        super(status, "PaymentException 서버 내부 오류입니다.");
    }

    public PaymentException() {
        this(BaseErrorCode.BAD_REQUEST);
    }
}
