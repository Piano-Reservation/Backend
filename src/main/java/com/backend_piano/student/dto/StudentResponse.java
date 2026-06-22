package com.backend_piano.student.dto;

import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record StudentResponse(
        @Schema(description = "학생 ID", example = "1") Long id,
        @Schema(description = "학번 (9자리)", example = "202312001") String studentNumber,
        @Schema(description = "이름", example = "김테스트") String name,
        @Schema(description = "생년월일", example = "2005-03-15") LocalDate birthDate,
        @Schema(description = "학년", example = "FRESHMAN") Grade grade,
        @Schema(description = "전공실기 과목", example = "PRACTICE_1") PracticeCourse practiceCourse,
        @Schema(description = "재학 상태", example = "ACTIVE") StudentStatus status
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
