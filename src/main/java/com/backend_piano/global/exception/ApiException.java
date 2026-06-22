package com.backend_piano.global.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public ApiException(BaseErrorCode errorCode) {
        super(errorCode.getMessage()); // 스택트레이스에 메시지 노출
        this.errorCode = errorCode;
    }
}
