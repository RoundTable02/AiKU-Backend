package login.exception;

import common.exception.BaseException;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.FORBIDDEN;
import static common.response.status.BaseErrorCode.INTERNAL_SERVER_ERROR;

@Getter
public class S3Exception extends BaseException {

    public S3Exception(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public S3Exception(String errorMessage) {
        super(INTERNAL_SERVER_ERROR, errorMessage);
    }

    public S3Exception() {
        super(INTERNAL_SERVER_ERROR, "이미지 서버 오류입니다.");
    }
}
