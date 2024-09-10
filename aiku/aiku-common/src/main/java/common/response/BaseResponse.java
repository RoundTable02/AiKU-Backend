package common.response;

import common.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseResponse<T> {
    private int code;
    private String message;
    private String requestId;
    private T result;

    //TODO 후에 MDC 스레드 UUID를 통해 requestId 변경
    public ResponseEntity<BaseResponse<T>> getResponseEntity(T result, StatusCode code) {
        BaseResponse<T> body = new BaseResponse<>(code.getCode(), code.getMessage(), null, result);
        return ResponseEntity.of(Optional.of(body));
    }
}
