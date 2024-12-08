package alarm.exception;

import common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MessageExceptionHandler {
    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<BaseErrorResponse> handle_MessagingException(MessagingException exception){
        log.error("MessagingExceptionHandler.handle_MessagingException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public ResponseEntity<BaseErrorResponse> handle_MemberNotFoundException(MemberNotFoundException exception){
        log.error("MessagingExceptionHandler.handle_MemberNotFoundException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }
}
