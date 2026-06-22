package com.backend_piano.global.exception;

import com.backend_piano.global.dto.ApiResponse;
import com.backend_piano.global.exception.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ApiException 전용 핸들러
    @ExceptionHandler(ApiException.class)
    public ApiResponse<Void> handleException(ApiException ex, HttpServletResponse response) {
        BaseErrorCode e = ex.getErrorCode();
        response.setStatus(e.getStatus().value());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    // 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletResponse response) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().getFirst();

        String errorMessage = String.format("[%s 필드 에러] %s", fieldError.getField(), fieldError.getDefaultMessage());

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.fail(400, errorMessage);
    }


    // 그 외 모든 예외 (서버 에러)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGlobalException(Exception ex, HttpServletResponse response) {
        log.error("Unhandled exception", ex);

        BaseErrorCode e = CommonErrorCode.SERVER_ERROR;
        response.setStatus(e.getStatus().value());

        return ApiResponse.fail(e.getCode(), ex.getMessage());
    }
}
