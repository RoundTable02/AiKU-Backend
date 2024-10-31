package common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseResponse<T> {
    private String requestId;
    private T result;

    public BaseResponse(T result) {
        this.requestId = MDC.get("request_id");
        this.result = result;
    }

    public static BaseResponse<BaseResultDto> getSimpleRes(Long id) {
        return new BaseResponse<>(MDC.get("request_id"), BaseResultDto.get(id));
    }
}
