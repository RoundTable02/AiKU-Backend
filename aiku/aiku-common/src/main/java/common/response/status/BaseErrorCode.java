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
    NO_SUCH_SCHEDULE(40003, "스케줄이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_BETTING(40004, "베팅이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_RACING(40005, "레이싱이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_TERM(40006, "약관이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_TEAM(40010, "팀에 소속된 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_TEAM(40011, "이미 팀에 소속되어 있습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_SCHEDULE(40012, "스케줄에 소속된 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_SCHEDULE(40013, "이미 스케줄에 소속되어 있습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_BETTING(40014, "베팅에 소속된 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_BETTING(40015, "이미 베팅에 소속되어 있습니다.", HttpStatus.BAD_REQUEST),
    CAN_NOT_EXIT(40016, "실행중인 스케줄이 있습니다.", HttpStatus.BAD_REQUEST),
    NO_TERM_SCHEDULE(40017, "스케줄이 아직 종료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_BETTING(40018, "이미 베팅이 존재합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_FCM_TOKEN(40019, "중복된 파이어베이스 토큰입니다.", HttpStatus.BAD_REQUEST),
    NO_FCM_TOKEN(40020, "파이어베이스 토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_RACING(40021, "레이싱에 소속된 유저가 아닙니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_RACING(40022, "중복된 레이싱입니다.", HttpStatus.BAD_REQUEST),

    NO_SCHEDULE_OWNER(40301, "스케줄장이 아닙니다.", HttpStatus.FORBIDDEN),
    NOT_AVAILABLE_SCHEDULE(40302, "이용 불가능한 스케줄입니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_SCHEDULE_UPDATE_TIME(40303, "스케줄 변경이 불가한 시간입니다.", HttpStatus.FORBIDDEN),
    NO_WAIT_SCHEDULE(40304, "변경이 불가능한 스케줄입니다.", HttpStatus.FORBIDDEN),
    FREE_MEMBER_LIMIT(40305, "깍두기 제한 기능입니다.", HttpStatus.FORBIDDEN),

    MEMBER_NOT_FOUND(40401, "멤버가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_WITH_TITLE(40402, "멤버가 칭호를 소유하지 않습니다.", HttpStatus.NOT_FOUND),

    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR),

    CAN_NOT_FIND_NEXT_SCHEDULE_OWNER(50001, "다음 스케줄 장을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    CAN_NOT_PROCESS_JSON(50002, "JSON에 관한 처리가 불가능합니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private int code; //서버 내부 오류 코드
    private String message; //서버 내부 오류 메세지
    private HttpStatus httpStatus; //Http 응답 status
}
