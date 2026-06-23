package com.backend_piano.global.exception;

import com.backend_piano.global.dto.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ApiException 전용 핸들러
    @ExceptionHandler(ApiException.class)
    public ApiResult<Void> handleException(ApiException ex, HttpServletResponse response) {
        BaseErrorCode e = ex.getErrorCode();
        response.setStatus(e.getStatus().value());
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    // @RequestParam 제약 조건 위반 (@Validated + @Max, @Min 등)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolation(ConstraintViolationException ex, HttpServletResponse response) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("입력값이 올바르지 않습니다.");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResult.fail(400, message);
    }

    // @RequestBody 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletResponse response) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .<String>map(fieldError -> String.format("[%s 필드 에러] %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .orElseGet(() -> ex.getBindingResult().getAllErrors().stream()
                        .findFirst()
                        .map(ObjectError::getDefaultMessage)
                        .orElse("입력값이 올바르지 않습니다."));

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResult.fail(400, errorMessage);
    }


    // 그 외 모든 예외 (서버 에러)
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleGlobalException(Exception ex, HttpServletResponse response) {
        log.error("Unhandled exception", ex);

        BaseErrorCode e = CommonErrorCode.SERVER_ERROR;
        response.setStatus(e.getStatus().value());

        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
