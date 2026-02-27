package com.bequitebtw.socialnetwork.domain.registration.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Valid
public record UserRegistrationDto(
		@NotNull
		@NotBlank
		String username,
		@Email
		@NotNull
		@NotBlank
		String email,
		@NotNull
		@NotBlank
		String password) {
};
