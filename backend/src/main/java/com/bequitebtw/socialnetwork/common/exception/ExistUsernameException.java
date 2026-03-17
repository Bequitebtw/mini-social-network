package com.bequitebtw.socialnetwork.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExistUsernameException extends RuntimeException {
	public final String username;

	@Override
	public String getMessage() {
		return String.format("User with username %s already exists", username);
	}
}