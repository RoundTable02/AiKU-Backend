package common.response;

import common.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseResponse<T> {
    private int code;
    private String message;
    private String requestId;
    private T result;

    //TODO 후에 MDC 스레드 UUID를 통해 requestId 변경

    public BaseResponse(T result, StatusCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.result = result;
    }

    public static BaseResponse<BaseResultDto> getSimpleRes(Long id, StatusCode code) {
        return new BaseResponse<>(code.getCode(), code.getMessage(), null, BaseResultDto.get(id));
    }
}
