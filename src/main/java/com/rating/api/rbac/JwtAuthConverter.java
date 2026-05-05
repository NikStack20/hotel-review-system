package com.rating.api.rbac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {

		Collection<GrantedAuthority> authorities = new ArrayList<>();

		List<String> groups = jwt.getClaimAsStringList("groups");

		if (groups != null) {
			authorities = groups.stream().map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
					.collect(Collectors.toList());
		}

		return new JwtAuthenticationToken(jwt, authorities);
	}
}
