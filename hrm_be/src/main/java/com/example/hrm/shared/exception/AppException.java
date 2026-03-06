package com.example.hrm.shared.exception;

public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int statusCode;

    public AppException(ErrorCode errorCode, int statusCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public AppException(ErrorCode errorCode, int statusCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

