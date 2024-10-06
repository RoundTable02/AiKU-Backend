package common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode implements StatusCode{

    //4XX 클라이언트 에러
    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED),
    FORBIDDEN(40300, HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN),
    NOT_FOUND(40400, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), HttpStatus.METHOD_NOT_ALLOWED),

    DELETED_DATA(40001, "삭제된 데이터 접근입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_TEAM(40002, "팀이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_TEAM(40003, "팀에 소속된 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_TEAM(40004, "이미 팀에 소속되어 있습니다.", HttpStatus.BAD_REQUEST),
    CAN_NOT_EXIT(40005, "실행중인 스케줄이 있습니다.", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_TERM(40006, "스케줄이 아직 종료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_BETTING(40007, "이미 베팅이 존재합니다.", HttpStatus.BAD_REQUEST),

    NOT_AVAILABLE_SCHEDULE(40301, "이용 불가능한 스케줄입니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_SCHEDULE_UPDATE_TIME(40302, "스케줄 변경이 불가한 시간입니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_SCHEDULE_UPDATE_STATUS(40303, "변경이 불가능한 스케줄 상태입니다.", HttpStatus.FORBIDDEN),
    FREE_MEMBER_LIMIT(40304, "깍두기 제한 기능입니다.", HttpStatus.FORBIDDEN),

    MEMBER_NOT_FOUND(40401, "멤버가 존재하지 않습니다.", HttpStatus.NOT_FOUND),


    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);

    private int code; //서버 내부 오류 코드
    private String message; //서버 내부 오류 메세지
    private HttpStatus httpStatus; //Http 응답 status
}
