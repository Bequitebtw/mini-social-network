package com.bequitebtw.socialnetwork.common.util;

import com.bequitebtw.socialnetwork.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.lifetime}")
	private Duration lifetime;


	public String generateJwt(UserDetails userDetails) {
		if (!(userDetails instanceof UserPrincipal principal)) {
			throw new IllegalArgumentException("Ожидается UserPrincipal.Class");
		}

		Map<String, Object> claims = new HashMap<>();
		Date issuedDate = new Date();
		Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		claims.put("roles", roles);
		log.info(roles.toString());

		return Jwts.builder()
				.claims(claims)
				.id(principal.getId().toString())
				.subject(userDetails.getUsername())
				.issuedAt(issuedDate)
				.expiration(expiredDate)
				.signWith(getSigningKey()).compact();
	}

	public String getId(String token) {
		return getClaim(token, Claims::getId);
	}

	public String getUsername(String token) {
		return getClaim(token, Claims::getSubject);
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoles(String token) {
		return getClaims(token).get("roles", List.class);
	}

	private Date getExpiration(String token) {
		return getClaim(token, Claims::getExpiration);
	}

	private <T> T getClaim(String token, Function<Claims, T> claimsResolvers) {
		Claims claims = getClaims(token);
		log.info(claims.toString());
		return claimsResolvers.apply(claims);
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build().parseSignedClaims(token).getPayload();
	}

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
