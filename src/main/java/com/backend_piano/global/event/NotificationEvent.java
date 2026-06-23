package com.backend_piano.global.event;

import com.backend_piano.notification.model.NotificationType;
import com.backend_piano.student.model.Student;
import java.util.Objects;

public record NotificationEvent(
        Long studentId,
        NotificationType type,
        String message
) {
    public static NotificationEvent of(Student student, NotificationType type, String message) {
        Objects.requireNonNull(student.getId());
        return new NotificationEvent(student.getId(), type, message);
    }
}
