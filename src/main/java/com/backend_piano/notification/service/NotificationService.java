package com.backend_piano.notification.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.exception.NotificationErrorCode;
import com.backend_piano.notification.model.Notification;
import com.backend_piano.notification.model.NotificationType;
import com.backend_piano.notification.repository.NotificationRepository;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.repository.StudentRepository;
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
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(StudentDetails studentDetails, int page, int size) {
        Student student = studentDetails.getStudent();
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        return notificationRepository.findByStudent(student, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional
    public void updateReadStatus(StudentDetails studentDetails, Long notificationId, boolean isRead) {
        if (!isRead) {
            throw new ApiException(NotificationErrorCode.CANNOT_MARK_AS_UNREAD);
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getStudent().getId().equals(studentDetails.getStudent().getId())) {
            throw new ApiException(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(StudentDetails studentDetails) {
        notificationRepository.markAllAsRead(studentDetails.getStudent());
    }

    @Transactional
    public NotificationResponse save(Long studentId, NotificationType type, String message) {
        Student student = studentRepository.getReferenceById(studentId);
        return NotificationResponse.from(
                notificationRepository.save(Notification.create(student, type, message)));
    }
}
