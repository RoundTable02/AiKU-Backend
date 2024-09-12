package common.exception;

import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

/**
 * 데이터 접근 권한이 없을때 throw
 * 디폴트 status는 403 Forbidden
 */
public class NoAuthorityException extends BaseException{

    public NoAuthorityException(StatusCode responseStatus, String errorMessage) {
        super(responseStatus, errorMessage);
    }

    public NoAuthorityException(String errorMessage) {
        super(BaseErrorCode.FORBIDDEN, errorMessage);
    }

    public NoAuthorityException() {
        super(BaseErrorCode.FORBIDDEN, "데이터 접근 권한이 없습니다.");
    }
}
