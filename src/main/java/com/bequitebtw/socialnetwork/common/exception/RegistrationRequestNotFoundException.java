package com.bequitebtw.socialnetwork.common.exception;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class RegistrationRequestNotFoundException extends RuntimeException {
	private final UUID registrationId;

	@Override
	public String getMessage() {
		return "Registration request with id: " + registrationId + " was not found";
	}
}
