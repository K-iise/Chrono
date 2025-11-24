package woowa.chrono.common.exception;

public enum ErrorCode {
    MEMBER_NOT_FOUND("[Error] 존재하지 않는 회원입니다."),
    ADMIN_NOT_FOUND("[Error] 존재하지 않는 관리자입니다."),
    NOT_ADMIN("[Error] 관리자 권한이 필요합니다."),
    DUPLICATE_MEMBER("[Error] 이미 존재하는 멤버입니다."),
    INVALID_TIME("[Error] 입력된 시간 값은 0보다 커야 합니다."),
    INVALID_POINT("[Error] 입력된 포인트는 0보다 커야 합니다."),
    LACK_POINT("[Error] 포인트가 부족합니다."),
    LACK_TIME("[Error] 이용 시간이 부족합니다."),
    PURCHASE_UNIT("[ERROR] 포인트의 구매 단위는 1000P 입니다."),
    HAVE_POINT("[ERROR] 보유한 포인트가 부족합니다."),
    EXIST_ADMIN("[ERROR] 이미 관리자가 존재합니다."),
    START_RECORD("[ERROR] 공부 기록을 끝낼 수 없습니다. 기록을 시작해주세요."),
    START_ALREADY("[ERROR] 이미 공부를 시작했습니다."),
    INVALID_EVENT_TIME("[ERROR] 유효하지 않은 이벤트 시간입니다. 시작/종료 시간을 확인해주세요."),
    EVENT_NOT_FOUND("[ERROR] 존재하지 않는 이벤트입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
