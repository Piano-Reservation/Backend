package com.backend_piano.student.model;

public enum StudentStatus {
    ACTIVE,       // 정상 재학 — 모든 기능 사용 가능
    LEAVE,        // 휴학 — 예약/입실 차단
    GRADUATED     // 졸업 — 예약/입실 차단
}