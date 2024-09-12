package common.exception;

import common.response.status.StatusCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException{
    protected StatusCode status;
    protected String errorMessage;

    public BaseException(StatusCode status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
