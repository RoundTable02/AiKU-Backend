package common.exception;

import common.response.status.BaseErrorCode;
import common.response.status.StatusCode;

import static common.response.status.BaseErrorCode.CAN_NOT_PROCESS_JSON;

public class JsonParseException extends BaseException{

    public JsonParseException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public JsonParseException(StatusCode status){
        this(status, "JsonParseException기 발생하였습니다.");
    }

    public JsonParseException(){
        this(CAN_NOT_PROCESS_JSON);
    }
}
