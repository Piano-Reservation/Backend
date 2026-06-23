package com.backend_piano.notification.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    NOTIFICATION_NOT_FOUND(3001, "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED(3002, "해당 알림에 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CANNOT_MARK_AS_UNREAD(3003, "알림 읽음 취소는 지원하지 않습니다.", HttpStatus.BAD_REQUEST),

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
