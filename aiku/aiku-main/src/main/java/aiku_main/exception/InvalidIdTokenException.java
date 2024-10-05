package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.FORBIDDEN;

@Getter
public class InvalidIdTokenException extends BaseException {

    public InvalidIdTokenException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public InvalidIdTokenException(String errorMessage) {
        super(FORBIDDEN, errorMessage);
    }

    public InvalidIdTokenException() {
        super(FORBIDDEN, "올바르지 않은 ID TOKEN입니다.");
    }
}
