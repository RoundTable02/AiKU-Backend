package payment.exception;

import common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import payment.exception.PaymentException;

import java.text.ParseException;

@Slf4j
@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<BaseErrorResponse> handle_PaymentException(PaymentException exception) {
        log.error("PaymentExceptionHandler.handle_PaymentException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public ResponseEntity<BaseErrorResponse> handle_MemberNotFoundException(MemberNotFoundException exception) {
        log.error("PaymentExceptionHandler.handle_MemberNotFoundException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }
}
