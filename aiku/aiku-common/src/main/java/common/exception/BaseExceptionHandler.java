package common.exception;

import common.response.BaseErrorResponse;
import common.response.status.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoHandlerFoundException.class, TypeMismatchException.class})
    public BaseErrorResponse handle_BadRequest(Exception e) {
        log.error("[BaseExceptionHandler: handle_BadRequest 호출]");
        return BaseErrorResponse.get(BaseErrorCode.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseErrorResponse handle_HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("[BaseExceptionHandler: handle_HttpRequestMethodNotSupportedException 호출]");
        return BaseErrorResponse.get(BaseErrorCode.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public BaseErrorResponse handle_InternalServerException(Exception e) {
        log.error("[BaseExceptionHandler: handle_InternalServerException 호출]", e);
        return BaseErrorResponse.get(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public BaseErrorResponse handle_RuntimeException(Exception e) {
        log.error("[BaseExceptionHandler: handle_RuntimeException 호출]", e);
        return BaseErrorResponse.get(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }
}
