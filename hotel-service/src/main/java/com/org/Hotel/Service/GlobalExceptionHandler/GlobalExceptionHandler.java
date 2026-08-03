package com.org.Hotel.Service.GlobalExceptionHandler;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.org.Hotel.Service.loadouts.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

	@RestControllerAdvice
	public class GlobalExceptionHandler { // JSON FORMAT
		
		 private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

		@ExceptionHandler(ConflictHandler.class)
		public ResponseEntity<Map<String, Object>> handleConflict(ConflictHandler ex, HttpServletRequest req) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("status", 409, "message", ex.getMessage(), "path", req.getRequestURI()));
		}
		
		@ExceptionHandler(DBExceptions.class)
		public ResponseEntity<ApiResponse> handleDBException(DBExceptions ex, HttpServletRequest req) {
		    log.warn("DB exception: {} - path={}", ex.getMessage(), req.getRequestURI());
		    ApiResponse response = ApiResponse.builder()
		            .success(false)
		            .status(HttpStatus.NOT_FOUND.value())     // 404
		            .message(ex.getMessage())
		            .path(req.getRequestURI())
		            .timestamp(Instant.now())
		            .build();
		    return ResponseEntity.status(HttpStatus.NOT_FOUND)
		            .contentType(MediaType.APPLICATION_JSON)
		            .body(response);
		}
		@ExceptionHandler(Exception.class)
		public ResponseEntity<ApiResponse> handleAll(Exception ex, HttpServletRequest req) {
		    log.error("Unhandled exception at path {}: {}", req.getRequestURI(), ex.getMessage(), ex);
		    ApiResponse resp = ApiResponse.builder()
		            .success(false)
		            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
		            .message("Internal server error")
		            .path(req.getRequestURI())
		            .timestamp(Instant.now())
		            .build();
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .contentType(MediaType.APPLICATION_JSON)
		            .body(resp);
		}
		

	    public GlobalExceptionHandler() {
	        // temporary debug log to ensure Spring created this bean
	        log.info(">>> GlobalExceptionHandler bean CREATED");
	        System.out.println(">>> GlobalExceptionHandler bean CREATED");
	    }



	}
