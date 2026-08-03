package com.lcwd.gateway.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

		return http.csrf(csrf -> csrf.disable()).authorizeExchange(exchange -> exchange

				// PUBLIC endpoints
				.pathMatchers("/login/**", "/oauth2/**").permitAll()

				// patching clients
				.pathMatchers("/users/**", "/ratings/**", "/hotels/**").permitAll()

				// everything else secure
				.anyExchange().authenticated()).oauth2Login(oauth -> {
				}).build();
	}
}
