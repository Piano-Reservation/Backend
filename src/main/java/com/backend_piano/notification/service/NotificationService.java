package com.backend_piano.notification.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.exception.NotificationErrorCode;
import com.backend_piano.notification.model.Notification;
import com.backend_piano.notification.model.NotificationType;
import com.backend_piano.notification.repository.NotificationRepository;
import com.backend_piano.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(StudentDetails studentDetails, int page, int size) {
        Student student = studentDetails.getStudent();
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByStudent(student, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional
    public void updateReadStatus(StudentDetails studentDetails, Long notificationId, boolean isRead) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getStudent().getId().equals(studentDetails.getStudent().getId())) {
            throw new ApiException(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        if (isRead) {
            notification.markAsRead();
        }
    }

    @Transactional
    public void updateAllReadStatus(StudentDetails studentDetails, boolean isRead) {
        if (isRead) {
            notificationRepository.markAllAsRead(studentDetails.getStudent());
        }
    }

    @Transactional
    public void save(Student student, NotificationType type, String message) {
        notificationRepository.save(Notification.create(student, type, message));
    }
}
