package common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseCode implements StatusCode {
    OK(2000, "요청 성공했습니다."),
    POST(2010, "등록 성공했습니다."),
    PATCH(2020, "수정 성공했습니다."),
    DELETE(2030, "삭제 성공했습니다."),
    GET(2040, "조회 성공했습니다."),

    ENTER(2011, "입장 성공했습니다."),
    EXIT(2012, "퇴장 성공했습니다.");

    private int code;
    private String message;
    private HttpStatus httpStatus;

    BaseCode(int code, String message) {
        this.code = code;
        this.message = message;
        httpStatus = HttpStatus.OK;
    }
}
