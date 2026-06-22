package com.backend_piano.notification.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.repository.NotificationRepository;
import com.backend_piano.student.model.Student;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(StudentDetails studentDetails) {
        Student student = studentDetails.getStudent();
        return notificationRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
