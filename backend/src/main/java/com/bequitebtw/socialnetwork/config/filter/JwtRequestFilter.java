package com.bequitebtw.socialnetwork.config.filter;

import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;
import com.bequitebtw.socialnetwork.common.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String username = null;
		UUID id = null;
		String jwt = null;
		List<String> roles = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			try {
				username = jwtUtil.getUsername(jwt);
				id = UUID.fromString(jwtUtil.getId(jwt));
				roles = jwtUtil.getRoles(jwt);
			} catch (ExpiredJwtException | SignatureException exception) {
				log.warn("EXPIRED OR NOT CORRECT SIGNED JWT");
			}
		}

		if (username != null && id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			List<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
			JwtUserPrincipal principal = new JwtUserPrincipal(username, id, roles);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					principal,
					null,
					grantedAuthorities
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}
}
