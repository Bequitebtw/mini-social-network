package com.bequitebtw.socialnetwork.domain.registration.service;

import com.bequitebtw.socialnetwork.common.exception.ExpiredRequestException;
import com.bequitebtw.socialnetwork.common.exception.RegistrationRequestNotFoundException;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationDto;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationResponse;
import com.bequitebtw.socialnetwork.domain.registration.entity.UserRegistration;
import com.bequitebtw.socialnetwork.common.exception.ExistEmailException;
import com.bequitebtw.socialnetwork.common.exception.ExistUsernameException;
import com.bequitebtw.socialnetwork.domain.registration.mapper.UserRegistrationMapper;
import com.bequitebtw.socialnetwork.domain.registration.repository.UserRegistrationRepository;
import com.bequitebtw.socialnetwork.notification.Notifier;
import com.bequitebtw.socialnetwork.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {
	private final UserRegistrationRepository userRegistrationRepository;
	private final UserRegistrationMapper userRegistrationMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final Notifier notifier;

	@Override
	public UserRegistrationResponse createUserRequest(UserRegistrationDto dto) {
		if (userService.existsByEmail(dto.email())) {
			throw new ExistEmailException(dto.email());
		}
		if (userService.existsByUsername(dto.username())) {
			throw new ExistUsernameException(dto.username());
		}

		UserRegistration request = userRegistrationRepository.findByEmail(dto.email())
				.orElseGet(() -> userRegistrationMapper.toEntity(dto));

		request.updateCredentials(
				dto.username(),
				passwordEncoder.encode(dto.password())
		);

		String token = request.refreshToken();

		userRegistrationRepository.save(request);
		notifier.sendVerificationToken(request.getEmail(), token);
		return userRegistrationMapper.toResponse(request);
	}

	@Override
	public UserRegistrationResponse confirmAccount(String token) {
		UserRegistration request = userRegistrationRepository.findByToken(token).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token not found"));
		if (userService.existsByEmail(request.getEmail())) {
			throw new ExistEmailException(request.getEmail());
		}
		if (userService.existsByUsername(request.getUsername())) {
			throw new ExistUsernameException(request.getUsername());
		}
		if (request.isExpired()) {
			throw new ExpiredRequestException();
		}
		userService.createUser(request.getEmail(), request.getUsername(), request.getPassword());
		userRegistrationRepository.delete(request);

		return userRegistrationMapper.toResponse(request);
	}

	@Override
	public UserRegistrationResponse resendToken(UUID registrationId) {
		UserRegistration request = userRegistrationRepository.findById(registrationId)
				.orElseThrow(() -> new RegistrationRequestNotFoundException(registrationId));

		String newToken = request.refreshToken();
		notifier.sendVerificationToken(request.getEmail(), newToken);
		return userRegistrationMapper.toResponse(userRegistrationRepository.save(request));
	}
}
