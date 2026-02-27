package com.bequitebtw.socialnetwork.domain.registration.service;

import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationDto;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationResponse;
import com.bequitebtw.socialnetwork.domain.registration.entity.UserRegistration;

import java.util.UUID;

public interface UserRegistrationService {
	UserRegistrationResponse createUserRequest(UserRegistrationDto userRegistrationDto);

	UserRegistrationResponse confirmAccount(String token);

	UserRegistrationResponse resendToken(UUID registrationId);
}
