package com.backend_piano.checkin.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CheckInErrorCode implements BaseErrorCode {

    CHECK_IN_INVALID_QR_TOKEN(8001, "유효하지 않은 QR 코드입니다.", HttpStatus.NOT_FOUND),
    CHECK_IN_ROOM_NOT_SUPPORTED(8002, "QR 인증은 1층과 3층 연습실만 가능합니다.", HttpStatus.BAD_REQUEST),
    CHECK_IN_RESTRICTED(8003, "현재 이용 제한 상태로 QR 인증할 수 없습니다.", HttpStatus.FORBIDDEN),
    CHECK_IN_RESERVATION_NOT_FOUND(8004, "현재 인증 가능한 예약이 없습니다.", HttpStatus.NOT_FOUND),
    CHECK_IN_TIME_NOT_ALLOWED(8005, "QR 인증은 예약 시작 시각부터 15분 이내에만 가능합니다.", HttpStatus.BAD_REQUEST),
    CHECK_IN_ALREADY_COMPLETED(8006, "이미 QR 인증이 완료된 예약입니다.", HttpStatus.CONFLICT),

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
