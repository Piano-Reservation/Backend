package com.backend_piano.reservation.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

    RESERVATION_NOT_FOUND(6001, "예약 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESERVATION_DATE_MUST_BE_TODAY(6002, "예약은 사용 당일에만 가능합니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_OPEN_YET(6003, "아직 예약 오픈 시간이 아닙니다.", HttpStatus.BAD_REQUEST),
    INVALID_RESERVATION_TIME_RANGE(6004, "예약 시간 범위가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_EXCEEDS_MAX_DURATION(6005, "1회 예약은 최대 2시간까지 가능합니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_TIME_CONFLICT(6006, "해당 시간대에는 이미 예약이 있습니다.", HttpStatus.CONFLICT),
    RESERVATION_RESTRICTED(6007, "현재 이용 제한 상태로 예약할 수 없습니다.", HttpStatus.FORBIDDEN),

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
