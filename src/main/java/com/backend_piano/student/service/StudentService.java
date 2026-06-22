package com.backend_piano.student.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.student.dto.StudentResponse;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    public StudentResponse getMe(StudentDetails studentDetails) {
        return StudentResponse.from(studentDetails.getStudent());
    }
}
