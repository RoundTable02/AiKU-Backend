package gateway.exception;

import common.exception.BaseException;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.FORBIDDEN;

@Getter
public class JwtAccessDeniedException extends BaseException {

    public JwtAccessDeniedException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public JwtAccessDeniedException(String errorMessage) {
        super(FORBIDDEN, errorMessage);
    }

    public JwtAccessDeniedException() {
        super(FORBIDDEN, "정상적이지 않은 접근입니다.");
    }
}
