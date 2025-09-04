package com.belvinard.inventory_management.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> errors;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.errors = errors;
    }

    public static ErrorResponse of(HttpStatus status, String error, Map<String, String> errors) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), error, errors);
    }
}
