package com.backend_piano.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // COMMON
    SERVER_ERROR(9999, "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
