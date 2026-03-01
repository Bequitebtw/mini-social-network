package com.bequitebtw.socialnetwork.auth.model;

import com.bequitebtw.socialnetwork.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Instant expiryDate;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public RefreshToken(User user, Instant expiryDate, String refreshToken) {
		this.token = refreshToken;
		this.user = user;
		this.expiryDate = expiryDate;
	}

	public boolean isExpired() {
		return expiryDate.isBefore(Instant.now());
	}
}