package com.backend_piano.basement.exception;

import com.backend_piano.global.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BasementOccupancyErrorCode implements BaseErrorCode {

    BASEMENT_ROOM_ONLY(7001, "지하 연습실만 입실 기록을 생성할 수 있습니다.", HttpStatus.BAD_REQUEST),
    BASEMENT_OCCUPANCY_RESTRICTED(7002, "현재 이용 제한 상태로 지하 연습실을 사용할 수 없습니다.", HttpStatus.FORBIDDEN),
    BASEMENT_ROOM_ALREADY_OCCUPIED(7003, "해당 지하 연습실은 현재 사용 중입니다.", HttpStatus.CONFLICT),
    BASEMENT_DUPLICATE_OCCUPANCY(7004, "이미 다른 지하 연습실을 사용 중입니다.", HttpStatus.CONFLICT),
    BASEMENT_OCCUPANCY_NOT_FOUND(7005, "지하 입실 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BASEMENT_OCCUPANCY_ACCESS_DENIED(7006, "해당 지하 입실 기록에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    BASEMENT_OCCUPANCY_ALREADY_EXITED(7007, "이미 퇴실 처리된 기록입니다.", HttpStatus.BAD_REQUEST),

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
