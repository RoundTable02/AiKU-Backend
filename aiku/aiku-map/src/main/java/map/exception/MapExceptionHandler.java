package map.exception;

import common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MapExceptionHandler {

    @ExceptionHandler({ScheduleException.class})
    public BaseErrorResponse handle_ScheduleException(ScheduleException exception){
        log.error("MapExceptionHandler.handle_ScheduleException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({RacingException.class})
    public BaseErrorResponse handle_RacingException(RacingException exception){
        log.error("MapExceptionHandler.handle_RacingException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public BaseErrorResponse handle_MemberNotFoundException(MemberNotFoundException exception){
        log.error("MapExceptionHandler.handle_MemberNotFoundException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({NotEnoughPointException.class})
    public BaseErrorResponse handle_NotEnoughPointException(NotEnoughPointException exception){
        log.error("MapExceptionHandler.handle_NotEnoughPointException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }
}
