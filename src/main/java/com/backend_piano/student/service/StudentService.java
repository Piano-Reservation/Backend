package com.backend_piano.student.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.student.dto.PasswordChangeRequest;
import com.backend_piano.student.dto.StudentResponse;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.student.exception.StudentErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Void changePassword(StudentDetails studentDetails, PasswordChangeRequest request, HttpServletRequest servletRequest) {
        Student student = studentRepository.findById(studentDetails.getStudent().getId())
                .orElseThrow(() -> new ApiException(StudentErrorCode.STUDENT_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), student.getPassword())) {
            throw new ApiException(StudentErrorCode.INVALID_CURRENT_PASSWORD);
        }

        student.changePassword(passwordEncoder.encode(request.newPassword()));

        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return null;
    }
}
