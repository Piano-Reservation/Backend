package com.backend_piano.room.model;

import com.backend_piano.global.model.BaseEntity;
import com.backend_piano.student.model.PracticeCourse;
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
        name = "room_allowed_courses",
        indexes = {
                @Index(name = "idx_room_allowed_courses_room_id", columnList = "room_id"),
                @Index(name = "idx_room_allowed_courses_course", columnList = "practice_course"),
                @Index(name = "uk_room_allowed_courses_room_course", columnList = "room_id, practice_course", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomAllowedCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PracticeCourse practiceCourse;

    public static RoomAllowedCourse create(Room room, PracticeCourse practiceCourse) {
        RoomAllowedCourse allowedCourse = new RoomAllowedCourse();
        allowedCourse.room = room;
        allowedCourse.practiceCourse = practiceCourse;
        return allowedCourse;
    }
}
