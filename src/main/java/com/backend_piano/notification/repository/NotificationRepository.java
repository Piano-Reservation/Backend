package com.backend_piano.notification.repository;

import com.backend_piano.notification.model.Notification;
import com.backend_piano.student.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentOrderByCreatedAtDesc(Student student);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.student = :student AND n.isRead = false")
    int markAllAsRead(@Param("student") Student student);
}
