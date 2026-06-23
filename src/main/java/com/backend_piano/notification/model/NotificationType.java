package com.backend_piano.notification.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    RESERVATION_CREATED("예약 생성 성공"),
    RESERVATION_CANCELLED("예약 취소 (본인 취소 또는 자동 NO_SHOW)"),
    CHECK_IN_COMPLETED("QR 체크인 성공"),
    NO_SHOW_MARKED("자동 미입실 처리"),
    RESTRICTION_APPLIED("이용 제한 시작"),
    RESTRICTION_LIFTED("이용 제한 종료"),
    BASEMENT_LONG_STAY("지하 미퇴실 4시간 초과");

    private final String description;
}
