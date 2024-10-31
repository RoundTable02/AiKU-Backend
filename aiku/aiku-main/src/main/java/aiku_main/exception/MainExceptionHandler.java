package aiku_main.exception;

import common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({ScheduleException.class})
    public BaseErrorResponse handle_ScheduleException(ScheduleException exception){
        log.error("MainExceptionHandler.handle_ScheduleException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({TeamException.class})
    public BaseErrorResponse handle_TeamException(TeamException exception){
        log.error("MainExceptionHandler.handle_TeamException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({BettingException.class})
    public BaseErrorResponse handle_BettingException(BettingException exception){
        log.error("MainExceptionHandler.handle_BettingException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }
}
