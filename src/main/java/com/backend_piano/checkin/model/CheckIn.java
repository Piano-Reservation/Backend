package com.backend_piano.checkin.model;

import com.backend_piano.global.model.BaseEntity;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.room.model.Room;
import com.backend_piano.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "check_ins",
        indexes = {
                @Index(name = "idx_check_ins_student_id", columnList = "student_id"),
                @Index(name = "idx_check_ins_reservation_id", columnList = "reservation_id"),
                @Index(name = "idx_check_ins_room_id", columnList = "room_id"),
                @Index(name = "idx_check_ins_checked_in_at", columnList = "checked_in_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckIn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "checked_in_at", nullable = false)
    private LocalDateTime checkedInAt;

    public static CheckIn create(
            Student student,
            Reservation reservation,
            Room room,
            LocalDateTime checkedInAt
    ) {
        CheckIn checkIn = new CheckIn();
        checkIn.student = student;
        checkIn.reservation = reservation;
        checkIn.room = room;
        checkIn.checkedInAt = checkedInAt;
        return checkIn;
    }
}
