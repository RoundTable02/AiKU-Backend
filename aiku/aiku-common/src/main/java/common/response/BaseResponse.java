package common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseResponse<T> {
    private String requestId;
    private T result;

    //TODO 후에 MDC 스레드 UUID를 통해 requestId 변경
    public BaseResponse(T result) {
        this.result = result;
    }

    public static BaseResponse<BaseResultDto> getSimpleRes(Long id) {
        return new BaseResponse<>(null, BaseResultDto.get(id));
    }
}
