package com.example.weatherforecasting.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<ApiError> handleBaseAppException(BaseAppException ex) {
        ApiError error = new ApiError(503, ex.getMessage());
        return ResponseEntity.status(503).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        ApiError error = new ApiError(500, "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
