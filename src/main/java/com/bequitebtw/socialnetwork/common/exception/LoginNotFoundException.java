package com.bequitebtw.socialnetwork.common.exception;


import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class LoginNotFoundException extends RuntimeException {
	private final String login;

	@Override
	public String getMessage() {
		return String.format("Login %s was not found", login);
	}

}
