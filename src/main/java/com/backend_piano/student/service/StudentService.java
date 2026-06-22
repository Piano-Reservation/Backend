package com.backend_piano.student.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.student.dto.PasswordChangeRequest;
import com.backend_piano.student.dto.StudentResponse;
import com.backend_piano.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    public StudentResponse getMe(StudentDetails studentDetails) {
        return StudentResponse.from(studentDetails.getStudent());
    }

    @Transactional
    public void changePassword(StudentDetails studentDetails, PasswordChangeRequest request) {
        var student = studentDetails.getStudent();

        if (!passwordEncoder.matches(request.currentPassword(), student.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        student.changePassword(passwordEncoder.encode(request.newPassword()));
        studentRepository.save(student);
    }
}
