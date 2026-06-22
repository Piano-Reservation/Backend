package com.backend_piano.student.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum StudentErrorCode implements BaseErrorCode {

    STUDENT_NOT_FOUND(2001, "학생을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CURRENT_PASSWORD(2002, "현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

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
