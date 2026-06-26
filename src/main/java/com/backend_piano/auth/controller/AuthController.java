package com.backend_piano.auth.controller;

import com.backend_piano.auth.dto.LoginRequest;
import com.backend_piano.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend_piano.global.dto.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = """
                    학번과 비밀번호로 로그인합니다.

                    - 학번: 9자리 숫자
                    - 비밀번호: 최소 6자리, 최대 72자리 (기본값: 생년월일 앞 6자리 숫자)

                    **테스트 계정 (비밀번호 공통: 000000)**
                    | 학번 | 이름 | 학년 |
                    |------|------|------|
                    | 202312001 | 김테스트 | FRESHMAN |
                    | 202212001 | 이테스트 | SOPHOMORE |
                    | 202112001 | 박테스트 | JUNIOR |
                    | 202012001 | 최테스트 | SENIOR |
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "학번 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "403", description = "비활성화된 계정")
    })
    @PostMapping
    public ApiResult<Void> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        return ApiResult.ok(authService.login(request, servletRequest, servletResponse));
    }
}
