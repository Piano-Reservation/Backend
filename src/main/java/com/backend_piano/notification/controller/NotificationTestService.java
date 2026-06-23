package com.backend_piano.notification.controller;

import com.backend_piano.global.event.NotificationEvent;
import com.backend_piano.notification.model.NotificationType;
import com.backend_piano.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 테스트용, 운영 배포 전 반드시 제거
@Service
@RequiredArgsConstructor
class NotificationTestService {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void publishEvent(Student student, NotificationType type, String message) {
        eventPublisher.publishEvent(NotificationEvent.of(student, type, message));
    }
}
