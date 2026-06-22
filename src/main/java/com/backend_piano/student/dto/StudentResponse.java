package com.backend_piano.student.dto;

import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.model.StudentStatus;
import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String studentNumber,
        String name,
        LocalDate birthDate,
        Grade grade,
        PracticeCourse practiceCourse,
        StudentStatus status
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
