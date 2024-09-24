package common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode implements StatusCode{

    //4XX 클라이언트 에러
    BAD_REQUEST(4000, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(4010, HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED),
    FORBIDDEN(4030, HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN),
    NOT_FOUND(4040, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(4050, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), HttpStatus.METHOD_NOT_ALLOWED),

    ALREADY_IN_TEAM(4003, "이미 팀에 소속되어 있습니다.", HttpStatus.BAD_REQUEST),
    CAN_NOT_EXIT(4004, "실행중인 스케줄이 있습니다.", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_TERM(4005, "스케줄이 아직 종료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_BETTING(4006, "이미 베팅이 존재합니다.", HttpStatus.BAD_REQUEST),

    NOT_AVAILABLE_SCHEDULE(4031, "이용 불가능한 스케줄입니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_SCHEDULE_UPDATE_TIME(4032, "스케줄 변경이 불가한 시간입니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_SCHEDULE_UPDATE_STATUS(4033, "변경이 불가능한 스케줄 상태입니다.", HttpStatus.FORBIDDEN),
    FREE_MEMBER_LIMIT(4034, "깍두기 제한 기능입니다.", HttpStatus.FORBIDDEN),


    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);

    private int code; //서버 내부 오류 코드
    private String message; //서버 내부 오류 메세지
    private HttpStatus httpStatus; //Http 응답 status
}
