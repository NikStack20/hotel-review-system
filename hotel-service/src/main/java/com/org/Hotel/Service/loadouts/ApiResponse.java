package com.org.Hotel.Service.loadouts;
import java.time.Instant;  
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApiResponse {

	private String message;
	private boolean success;
	private int status;
	private String path;     // request URI (optional)
	private Instant timestamp; 

}