package com.backend_piano.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    int getCode();

    String getMessage();

    HttpStatus getStatus();
}
