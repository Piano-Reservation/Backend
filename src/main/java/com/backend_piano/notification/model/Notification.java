package com.backend_piano.notification.model;

import com.backend_piano.global.model.BaseEntity;
import com.backend_piano.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_student_id", columnList = "student_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    public static Notification create(Student student, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.student = student;
        notification.type = type;
        notification.message = message;
        notification.isRead = false;
        return notification;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
