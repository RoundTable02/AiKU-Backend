package map.exception;

import common.exception.BaseException;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.MEMBER_NOT_FOUND;

@Getter
public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public MemberNotFoundException(String errorMessage) {
        super(MEMBER_NOT_FOUND, errorMessage);
    }

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND, "멤버가 존재하지 않습니다.");
    }
}
