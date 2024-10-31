package common.response;

import common.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseErrorResponse {
    private int code;
    private String message;
    private String requestId;

    public static ResponseEntity<BaseErrorResponse> get(StatusCode code) {
        BaseErrorResponse res = new BaseErrorResponse(code.getCode(), code.getMessage(), MDC.get("request_id"));
        return new ResponseEntity<>(res, code.getHttpStatus());
    }

    public BaseErrorResponse(int code, String message) {
        this.requestId = MDC.get("request_id");
        this.code = code;
        this.message = message;
    }
}
