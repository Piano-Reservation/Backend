package com.backend_piano.auth.service;

import com.backend_piano.student.model.Student;
import com.backend_piano.student.model.StudentStatus;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class StudentDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Student student;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    @NonNull
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    @NonNull
    public String getUsername() {
        return student.getStudentNumber();
    }

    // LEAVE, GRADUATED 상태 학생은 로그인 차단
    @Override
    public boolean isEnabled() {
        return student.getStatus() == StudentStatus.ACTIVE;
    }
}
