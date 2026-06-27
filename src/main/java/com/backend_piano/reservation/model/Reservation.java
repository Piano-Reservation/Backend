package com.backend_piano.reservation.model;

import com.backend_piano.global.model.BaseEntity;
import com.backend_piano.room.model.Room;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_reservations_student_id", columnList = "student_id"),
                @Index(name = "idx_reservations_room_date", columnList = "room_id, reservation_date"),
                @Index(name = "idx_reservations_student_date", columnList = "student_id, reservation_date"),
                @Index(name = "idx_reservations_date_status", columnList = "reservation_date, status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column
    private LocalDateTime checkedInAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column(length = 255)
    private String cancelReason;

    public static Reservation create(
            Student student,
            Room room,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        Reservation reservation = new Reservation();
        reservation.student = student;
        reservation.room = room;
        reservation.reservationDate = reservationDate;
        reservation.startTime = startTime;
        reservation.endTime = endTime;
        reservation.status = ReservationStatus.RESERVED;
        return reservation;
    }

    public void markCheckedIn(LocalDateTime checkedInAt) {
        this.status = ReservationStatus.CHECKED_IN;
        this.checkedInAt = checkedInAt;
    }

    public void markCompleted() {
        this.status = ReservationStatus.COMPLETED;
    }

    public void cancel(String cancelReason, LocalDateTime cancelledAt) {
        this.status = ReservationStatus.CANCELLED;
        this.cancelReason = cancelReason;
        this.cancelledAt = cancelledAt;
    }

    public void markNoShow() {
        this.status = ReservationStatus.NO_SHOW;
    }
}
