package com.User.Service.GlobalExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler { // JSON FORMAT

    @ExceptionHandler(DBExceptions.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(DBExceptions ex) {

        String message = ex.getMessage();
        ApiResponse response = ApiResponse.builder().message(message).success(true).status(HttpStatus.NOT_FOUND)
                .build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictHandler.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictHandler ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("status", 409, "message", ex.getMessage(), "path", req.getRequestURI()));
    }

}











