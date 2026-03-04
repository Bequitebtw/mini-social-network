package com.bequitebtw.socialnetwork.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private UUID id;
	@Column(name = "username", nullable = false, length = 20, unique = true)
	private String username;
	@Column(name = "email", unique = true, nullable = false, length = 30)
	private String email;
	@Column(name = "password", nullable = false)
	private String password;
	@Column(name = "isBanned")
	boolean isBanned = false;
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;
}
