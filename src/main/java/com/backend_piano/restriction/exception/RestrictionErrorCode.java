package com.backend_piano.restriction.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum RestrictionErrorCode implements BaseErrorCode {

    RESTRICTION_NOT_FOUND(4001, "이용 제한 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

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
