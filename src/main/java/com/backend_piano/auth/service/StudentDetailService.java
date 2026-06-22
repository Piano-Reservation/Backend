package com.backend_piano.auth.service;

import com.backend_piano.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentDetailService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String studentNumber) throws UsernameNotFoundException {
        return studentRepository.findByStudentNumber(studentNumber)
                .map(StudentDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(studentNumber));
    }
}
