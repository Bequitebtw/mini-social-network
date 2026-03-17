package com.bequitebtw.socialnetwork.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Valid
@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor
public class UserDto {
	@NotNull
	@NotBlank
	private String username;
	@Email
	@NotNull
	@NotBlank
	private String email;
	@NotNull
	@NotBlank
	private String password;
}
