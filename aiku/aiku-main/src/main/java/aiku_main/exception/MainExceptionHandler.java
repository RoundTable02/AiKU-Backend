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

    @ExceptionHandler({FcmException.class})
    public BaseErrorResponse handle_FcmException(FcmException exception){
        log.error("MainExceptionHandler.handle_FcmException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({InvalidIdTokenException.class})
    public BaseErrorResponse handle_InvalidIdTokenException(InvalidIdTokenException exception){
        log.error("MainExceptionHandler.handle_InvalidIdTokenException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({JwtAccessDeniedException.class})
    public BaseErrorResponse handle_JwtAccessDeniedException(JwtAccessDeniedException exception){
        log.error("MainExceptionHandler.handle_JwtAccessDeniedException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public BaseErrorResponse handle_MemberNotFoundException(MemberNotFoundException exception){
        log.error("MainExceptionHandler.handle_MemberNotFoundException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({TermException.class})
    public BaseErrorResponse handle_TermException(TermException exception){
        log.error("MainExceptionHandler.handle_TermException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({S3Exception.class})
    public BaseErrorResponse handle_S3Exception(S3Exception exception){
        log.error("MainExceptionHandler.handle_S3Exception <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }

    @ExceptionHandler({TitleException.class})
    public BaseErrorResponse handle_TitleException(TitleException exception){
        log.error("MainExceptionHandler.handle_TitleException <{}> {}", exception.getStatus().getMessage(), exception);
        return new BaseErrorResponse(exception.getStatus().getCode(), exception.getStatus().getMessage());
    }
}
