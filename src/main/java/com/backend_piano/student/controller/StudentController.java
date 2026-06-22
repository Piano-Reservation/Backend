package com.backend_piano.student.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResponse;
import com.backend_piano.student.dto.PasswordChangeRequest;
import com.backend_piano.student.dto.StudentResponse;
import com.backend_piano.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    public ApiResponse<StudentResponse> getMe(@AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResponse.ok(studentService.getMe(studentDetails));
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody @Valid PasswordChangeRequest request) {
        return ApiResponse.ok(studentService.changePassword(studentDetails, request));
    }
}
