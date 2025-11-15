package woowa.chrono.exception;

public enum ErrorCode {
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다."),
    ADMIN_NOT_FOUND("존재하지 않는 관리자입니다."),
    NOT_ADMIN("관리자 권한이 필요합니다."),
    DUPLICATE_MEMBER("이미 존재하는 멤버입니다."),
    INVALID_TIME("시간 값은 0보다 커야 합니다."),
    INVALID_POINT("포인트는 0보다 커야 합니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
