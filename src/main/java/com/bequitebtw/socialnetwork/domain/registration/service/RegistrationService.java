package com.bequitebtw.socialnetwork.domain.registration.service;

import com.bequitebtw.socialnetwork.domain.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.domain.registration.dto.RegistrationResponse;

import java.util.UUID;

public interface RegistrationService {
	RegistrationResponse createRegistrationRequest(RegistrationRequest registrationRequest);

	RegistrationResponse confirmAccount(String token);

	RegistrationResponse resendToken(UUID registrationId);
}
