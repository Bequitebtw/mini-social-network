package com.bequitebtw.socialnetwork.registration.service;

import com.bequitebtw.socialnetwork.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationResponse;

import java.util.UUID;

public interface RegistrationService {
	RegistrationResponse createRegistrationRequest(RegistrationRequest registrationRequest);

	RegistrationResponse confirmAccount(String token);

	RegistrationResponse resendToken(UUID registrationId);
}
