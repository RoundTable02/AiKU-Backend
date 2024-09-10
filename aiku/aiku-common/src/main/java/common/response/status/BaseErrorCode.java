package common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode implements StatusCode{

    //4XX 클라이언트 에러
    BAD_REQUEST(4000, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    UNAUTHORIZED(4001, HttpStatus.UNAUTHORIZED.getReasonPhrase()),
    FORBIDDEN(4003, HttpStatus.FORBIDDEN.getReasonPhrase()),
    NOT_FOUND(4004, HttpStatus.NOT_FOUND.getReasonPhrase()),
    METHOD_NOT_ALLOWED(4005, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase()),

    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR.name());

    private int code;
    private String message;
}
