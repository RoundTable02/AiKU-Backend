package gateway.exception;

import lombok.Getter;

@Getter
public class JwtAccessDeniedException extends RuntimeException{

    public JwtAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtAccessDeniedException(String errorMessage) {
        super(errorMessage, null);
    }

    public JwtAccessDeniedException() {
        super("정상적이지 않은 접근입니다.", null);
    }
}
