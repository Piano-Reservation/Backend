package com.backend_piano.student.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.student.dto.PasswordChangeRequest;
import com.backend_piano.student.dto.StudentResponse;
import com.backend_piano.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "내 정보 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/me")
    public ApiResult<StudentResponse> getMe(@AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResult.ok(studentService.getMe(studentDetails));
    }

    @Operation(summary = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "401", description = "미인증")
    })
    @PutMapping("/me/password")
    public ApiResult<Void> changePassword(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody @Valid PasswordChangeRequest request) {
        return ApiResult.ok(studentService.changePassword(studentDetails, request));
    }
}
