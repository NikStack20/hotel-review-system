package com.User.Service.GlobalExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApiResponse {

    private String message;
    private boolean success;
    private HttpStatus status;

}
