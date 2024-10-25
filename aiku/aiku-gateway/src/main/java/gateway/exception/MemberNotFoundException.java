package gateway.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException{

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFoundException(String errorMessage) {
        super(errorMessage, null);
    }

    public MemberNotFoundException() {
        super("멤버가 존재하지 않습니다.", null);
    }
}
