package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.FORBIDDEN;
import static common.response.status.BaseErrorCode.INTERNAL_SERVER_ERROR;

@Getter
public class PointChangeFailException extends BaseException {

    public PointChangeFailException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public PointChangeFailException(String errorMessage) {
        super(INTERNAL_SERVER_ERROR, errorMessage);
    }

    public PointChangeFailException() {
        super(INTERNAL_SERVER_ERROR, "포인트 증감 단계에서 오류가 발생하였습니다.");
    }
}
