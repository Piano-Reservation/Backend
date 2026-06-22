package com.backend_piano.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * API 요청에 대한 표준 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiResult<T> {
    private int code;

    private String message;

    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "성공", data);
    }

    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
