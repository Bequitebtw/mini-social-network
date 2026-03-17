package com.bequitebtw.socialnetwork.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class ExistEmailException extends RuntimeException {
	public final String email;

	@Override
	public String getMessage() {
		return String.format("User with email %s already exists", email);
	}
}
