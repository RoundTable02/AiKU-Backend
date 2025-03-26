package common.domain;

public enum PaymentStatus {
    AWAIT, // 결제 요청 기본 상태
    ACCEPT, // 결제 성공
    DENIED, // 서버 내부 오류로 인한 거절
    CANCLE, // 직접 요청 결제 취소
    INVALID // 구글 결제 상태 처리 불가
}
