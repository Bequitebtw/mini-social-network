package com.bequitebtw.socialnetwork.auth.cookie;

import com.bequitebtw.socialnetwork.auth.dto.RefreshTokenResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class RefreshTokenCookieFactory {

	public ResponseCookie create(RefreshTokenResponse refreshTokenResponse) {
		return ResponseCookie.from("refresh_token", refreshTokenResponse.token())
				.httpOnly(true)
				.secure(false) // true в production https
				.sameSite("Strict")
				.path("/api/auth/refresh")
				.maxAge(Duration.between(Instant.now(), refreshTokenResponse.expiryDate()))
				.build();
	}

	public ResponseCookie delete() {
		return ResponseCookie.from("refresh_token", "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Strict")
				.path("/api/auth/refresh")
				.maxAge(0)
				.build();
	}
}
