package common.exception;

import common.response.BaseErrorResponse;
import common.response.status.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler({NoHandlerFoundException.class, TypeMismatchException.class})
    public ResponseEntity<BaseErrorResponse> handle_BadRequest(Exception e) {
        log.error("[BaseExceptionHandler: handle_BadRequest 호출]");
        return BaseErrorResponse.get(BaseErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse> handle_HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("[BaseExceptionHandler: handle_HttpRequestMethodNotSupportedException 호출]");
        return BaseErrorResponse.get(BaseErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<BaseErrorResponse> handle_InternalServerException(Exception e) {
        log.error("[BaseExceptionHandler: handle_InternalServerException 호출]", e);
        return BaseErrorResponse.get(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseErrorResponse> handle_RuntimeException(Exception e) {
        log.error("[BaseExceptionHandler: handle_RuntimeException 호출]", e);
        return BaseErrorResponse.get(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseErrorResponse> handle_NoSuchElementException(NoSuchElementException e) {
        log.error("[BaseExceptionHandler: handle_NoAuthorityException 호출]", e);
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);
        return BaseErrorResponse.get(BaseErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(NoAuthorityException.class)
    public ResponseEntity<BaseErrorResponse> handle_NoAuthorityException(NoAuthorityException e) {
        log.error("[BaseExceptionHandler: handle_NoAuthorityException 호출]", e);
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);
        return BaseErrorResponse.get(e.getStatus());
    }
}
