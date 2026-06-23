package com.backend_piano.global.event;

import com.backend_piano.notification.model.NotificationType;
import com.backend_piano.student.model.Student;

public record NotificationEvent(
        Long studentId,
        NotificationType type,
        String message
) {
    public static NotificationEvent of(Student student, NotificationType type, String message) {
        return new NotificationEvent(student.getId(), type, message);
    }
}
