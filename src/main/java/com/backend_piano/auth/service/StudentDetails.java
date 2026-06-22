package com.backend_piano.auth.service;

import com.backend_piano.student.model.Student;
import com.backend_piano.student.model.StudentStatus;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class StudentDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Student student;

    public Student getStudent() {
        return student;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    public String getUsername() {
        return student.getStudentNumber();
    }

    // LEAVE, GRADUATED 상태 학생은 로그인 차단
    @Override
    public boolean isEnabled() {
        return student.getStatus() == StudentStatus.ACTIVE;
    }
}
