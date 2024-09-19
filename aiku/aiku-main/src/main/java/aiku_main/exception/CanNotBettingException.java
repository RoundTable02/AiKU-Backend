package aiku_main.exception;

import common.exception.BaseException;
import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;
import lombok.Getter;

import static common.response.status.BaseErrorCode.ALREADY_HAS_BETTING;

@Getter
public class CanNotBettingException extends BaseException {

    public CanNotBettingException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public CanNotBettingException(String errorMessage) {
        super(ALREADY_HAS_BETTING, errorMessage);
    }

    public CanNotBettingException() {
        super(ALREADY_HAS_BETTING, "베팅을 등록할 수 없습니다.");
    }
}
