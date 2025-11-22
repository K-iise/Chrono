package woowa.chrono.common.exception;

public class ChronoException extends RuntimeException {
    private final ErrorCode errorCode;

    public ChronoException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
