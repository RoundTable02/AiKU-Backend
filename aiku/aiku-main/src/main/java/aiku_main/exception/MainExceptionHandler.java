package aiku_main.exception;

import common.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({ScheduleException.class})
    public ResponseEntity<BaseErrorResponse> handle_ScheduleException(ScheduleException exception){
        log.error("MainExceptionHandler.handle_ScheduleException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({TeamException.class})
    public ResponseEntity<BaseErrorResponse> handle_TeamException(TeamException exception){
        log.error("MainExceptionHandler.handle_TeamException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({BettingException.class})
    public ResponseEntity<BaseErrorResponse> handle_BettingException(BettingException exception){
        log.error("MainExceptionHandler.handle_BettingException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({FcmException.class})
    public ResponseEntity<BaseErrorResponse> handle_FcmException(FcmException exception){
        log.error("MainExceptionHandler.handle_FcmException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({InvalidIdTokenException.class})
    public ResponseEntity<BaseErrorResponse> handle_InvalidIdTokenException(InvalidIdTokenException exception){
        log.error("MainExceptionHandler.handle_InvalidIdTokenException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({JwtAccessDeniedException.class})
    public ResponseEntity<BaseErrorResponse> handle_JwtAccessDeniedException(JwtAccessDeniedException exception){
        log.error("MainExceptionHandler.handle_JwtAccessDeniedException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public ResponseEntity<BaseErrorResponse> handle_MemberNotFoundException(MemberNotFoundException exception){
        log.error("MainExceptionHandler.handle_MemberNotFoundException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({TermException.class})
    public ResponseEntity<BaseErrorResponse> handle_TermException(TermException exception){
        log.error("MainExceptionHandler.handle_TermException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({S3Exception.class})
    public ResponseEntity<BaseErrorResponse> handle_S3Exception(S3Exception exception){
        log.error("MainExceptionHandler.handle_S3Exception <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }

    @ExceptionHandler({TitleException.class})
    public ResponseEntity<BaseErrorResponse> handle_TitleException(TitleException exception){
        log.error("MainExceptionHandler.handle_TitleException <{}> {}", exception.getStatus().getMessage(), exception);

        return BaseErrorResponse.get(exception);
    }
}
