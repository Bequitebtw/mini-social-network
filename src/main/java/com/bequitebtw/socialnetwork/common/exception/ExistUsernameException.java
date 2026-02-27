package com.bequitebtw.socialnetwork.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExistUsernameException extends RuntimeException {
	public final String username;

	@Override
	public String getMessage() {
		return String.format("User with username %s already exists", username);
	}
}