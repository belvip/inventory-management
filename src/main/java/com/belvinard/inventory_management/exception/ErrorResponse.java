package com.belvinard.inventory_management.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> errors
) {
    public static ErrorResponse of(HttpStatus status, String error, Map<String, String> errors) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), error, errors);
    }
}
