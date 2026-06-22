package com.backend_piano.student.dto;

import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record StudentResponse(
        @Schema(description = "학생 ID") Long id,
        @Schema(description = "학번 (9자리)") String studentNumber,
        @Schema(description = "이름") String name,
        @Schema(description = "생년월일") LocalDate birthDate,
        @Schema(description = "학년") Grade grade,
        @Schema(description = "전공실기 과목") PracticeCourse practiceCourse,
        @Schema(description = "재학 상태") StudentStatus status
) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentNumber(),
                student.getName(),
                student.getBirthDate(),
                student.getGrade(),
                student.getPracticeCourse(),
                student.getStatus()
        );
    }
}
