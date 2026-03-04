package com.bequitebtw.socialnetwork.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
		@NotBlank
		@NotNull
		String login,
		@NotNull
		@NotBlank
		String password) {
}
