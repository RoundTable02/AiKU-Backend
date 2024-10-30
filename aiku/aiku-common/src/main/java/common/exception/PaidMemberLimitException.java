package common.exception;

import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

public class PaidMemberLimitException extends BaseException{
    public PaidMemberLimitException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public PaidMemberLimitException(StatusCode status) {
        this(status, "유료 멤버 제한 기능입니다.");
    }

    public PaidMemberLimitException() {
        this(BaseErrorCode.FREE_MEMBER_LIMIT);
    }
}
