package com.bequitebtw.socialnetwork.registration.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "registrations")
@NoArgsConstructor
public class Registration {
	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "username", nullable = false, length = 20)
	private String username;

	@Column(name = "email", unique = true, nullable = false, length = 30)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "token", nullable = false, unique = true, length = 64)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

	@Column(name = "last_resend_at", nullable = false)
	private LocalDateTime lastResendAt = LocalDateTime.now();

	public boolean canResend() {
		return lastResendAt == null || lastResendAt.plusMinutes(1).isBefore(LocalDateTime.now());
	}


	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public String refreshToken(String token) {
		if (!canResend()) {
			throw new IllegalStateException("Resend cooldown");
		}
		this.token = token;
		this.lastResendAt = LocalDateTime.now();
		this.expiresAt = LocalDateTime.now().plusHours(24);
		return token;
	}

	public void updateCredentials(String username, String passwordHash) {
		this.username = username;
		this.password = passwordHash;
	}

}
