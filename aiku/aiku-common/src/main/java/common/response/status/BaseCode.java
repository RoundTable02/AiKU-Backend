package common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseCode implements StatusCode {
    OK(2000, "요청 성공했습니다."),
    POST(2001, "등록 성공했습니다."),
    PATCH(2002, "수정 성공했습니다."),
    DELETE(2003, "삭제 성공했습니다."),
    GET(2004, "조회 성공했습니다.");

    private int code;
    private String message;
}
