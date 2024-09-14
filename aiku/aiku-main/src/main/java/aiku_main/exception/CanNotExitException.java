package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;
import lombok.Getter;

@Getter
public class CanNotExitException extends BaseException {

    public CanNotExitException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public CanNotExitException(StatusCode status) {
        super(status, "퇴장 불가능합니다.");
    }

    public CanNotExitException() {
        super(BaseErrorCode.CAN_NOT_EXIT, "퇴장 불가능합니다.");
    }
}
