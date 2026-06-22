package com.backend_piano.student.model;

import com.backend_piano.global.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_students_student_number", columnList = "studentNumber", unique = true),
                @Index(name = "idx_students_practice_course", columnList = "practiceCourse")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity implements Serializable {

    // 세션 직렬화 시 클래스 버전 식별자. 변경하면 기존 세션 역직렬화가 깨지므로 하위 호환 변경에만 유지.
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 9)
    private String studentNumber;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate;

    // 표시·통계용 학년. 매 학기 갱신.
    // 예약 검증에는 사용하지 않음 — 검증은 practiceCourse 기준.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Grade grade;

    // 전공실기 강의 번호. 예약 검증의 기준.
    // 학생이 이번 학기에 듣는 전공실기 과목 (1~8).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PracticeCourse practiceCourse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status;

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public static Student create(
            String studentNumber,
            String name,
            String encodedPassword,
            LocalDate birthDate,
            Grade grade,
            PracticeCourse practiceCourse
    ) {
        Student student = new Student();
        student.studentNumber = studentNumber;
        student.name = name;
        student.password = encodedPassword;
        student.birthDate = birthDate;
        student.grade = grade;
        student.practiceCourse = practiceCourse;
        student.status = StudentStatus.ACTIVE;
        return student;
    }
}
