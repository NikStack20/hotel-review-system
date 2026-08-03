package com.lcwd.gateway.ApiGateway.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthResponse {

	private String userId;
	private String accessToken;
	private String refreshToken;
	private long expiresAt;
	private List<String> authorities;

	public AuthResponse(String userId, String accessToken, String refreshToken, long expiresAt,
			List<String> authorities) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
		this.authorities = authorities;
	}

	// getters
}
