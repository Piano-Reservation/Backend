package com.backend_piano.basement.model;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "basement_occupancies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_basement_occupancies_active_student", columnNames = "active_student_id"),
                @UniqueConstraint(name = "uk_basement_occupancies_active_room", columnNames = "active_room_id")
        },
        indexes = {
                @Index(name = "idx_basement_occupancies_student_status", columnList = "student_id, status"),
                @Index(name = "idx_basement_occupancies_room_status", columnList = "room_id, status"),
                @Index(name = "idx_basement_occupancies_entered_at", columnList = "entered_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasementOccupancy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BasementOccupancyStatus status;

    @Column(name = "entered_at", nullable = false)
    private LocalDateTime enteredAt;

    @Column(name = "exited_at")
    private LocalDateTime exitedAt;

    @Column(name = "active_student_id", unique = true)
    private Long activeStudentId;

    @Column(name = "active_room_id", unique = true)
    private Long activeRoomId;

    public static BasementOccupancy create(Student student, Room room, LocalDateTime enteredAt) {
        BasementOccupancy occupancy = new BasementOccupancy();
        occupancy.student = student;
        occupancy.room = room;
        occupancy.status = BasementOccupancyStatus.IN_USE;
        occupancy.enteredAt = enteredAt;
        occupancy.activeStudentId = student.getId();
        occupancy.activeRoomId = room.getId();
        return occupancy;
    }

    public void exit(LocalDateTime exitedAt) {
        this.status = BasementOccupancyStatus.EXITED;
        this.exitedAt = exitedAt;
        this.activeStudentId = null;
        this.activeRoomId = null;
    }
}
