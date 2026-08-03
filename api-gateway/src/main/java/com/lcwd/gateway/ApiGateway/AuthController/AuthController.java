package com.lcwd.gateway.ApiGateway.AuthController;

import com.lcwd.gateway.ApiGateway.models.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthController {

	@GetMapping("/auth/login")
	public Mono<ResponseEntity<AuthResponse>> login(
			@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
			@AuthenticationPrincipal OidcUser user) {

		System.out.println("AUTH CONTROLLER HIT");
		return Mono.fromSupplier(() -> {

			List<String> authorities = user.getAuthorities().stream().map(a -> a.getAuthority())
					.collect(Collectors.toList());

			String accessToken = client.getAccessToken().getTokenValue();

			String refreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;

			long expiresAt = client.getAccessToken().getExpiresAt().toEpochMilli();

			AuthResponse response = new AuthResponse(user.getSubject(), accessToken, refreshToken, expiresAt,
					authorities);

			return ResponseEntity.ok(response);
		});
	}
}
