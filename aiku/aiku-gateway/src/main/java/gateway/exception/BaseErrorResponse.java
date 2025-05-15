package gateway.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseErrorResponse {
    private int code;
    private String message;
    private String requestId;

    public BaseErrorResponse(int code, String message) {
        this.requestId = MDC.get("request_id");
        this.code = code;
        this.message = message;
    }
}
