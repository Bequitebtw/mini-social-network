package com.bequitebtw.socialnetwork.domain.authentication.dto;

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
