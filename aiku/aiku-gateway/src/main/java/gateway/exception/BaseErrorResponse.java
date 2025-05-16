package gateway.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseErrorResponse {
    private int code;
    private String message;
    private String requestId;

    public BaseErrorResponse(int code, String message) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("request_id", requestId);

        this.requestId = requestId;
        this.code = code;
        this.message = message;
    }
}
