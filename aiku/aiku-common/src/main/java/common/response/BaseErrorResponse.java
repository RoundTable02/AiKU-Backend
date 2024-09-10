package common.response;

import common.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseErrorResponse {
    private int code;
    private String message;
    private String requestId;

    public ResponseEntity<BaseErrorResponse> getResponseEntity(StatusCode code) {
        BaseErrorResponse body = new BaseErrorResponse(code.getCode(), code.getMessage(), null);
        return ResponseEntity.of(Optional.of(body));
    }
}
