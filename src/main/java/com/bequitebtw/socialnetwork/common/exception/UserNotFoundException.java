package com.bequitebtw.socialnetwork.common.exception;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UserNotFoundException extends RuntimeException {
	private final UUID userId;

	@Override
	public String getMessage() {
		return String.format("User with id %s not found", userId);
	}
}
