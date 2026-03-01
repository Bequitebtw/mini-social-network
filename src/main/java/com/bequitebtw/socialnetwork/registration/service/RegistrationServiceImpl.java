package com.bequitebtw.socialnetwork.registration.service;

import com.bequitebtw.socialnetwork.common.exception.*;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationResponse;
import com.bequitebtw.socialnetwork.registration.model.Registration;
import com.bequitebtw.socialnetwork.registration.mapper.RegistrationMapper;
import com.bequitebtw.socialnetwork.registration.repository.RegistrationRepository;
import com.bequitebtw.socialnetwork.notification.Notifier;
import com.bequitebtw.socialnetwork.security.TokenGenerator;
import com.bequitebtw.socialnetwork.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
	private final RegistrationRepository registrationRepository;
	private final RegistrationMapper registrationMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final Notifier notifier;
	private final TokenGenerator tokenGenerator;

	@Override
	public RegistrationResponse createRegistrationRequest(RegistrationRequest dto) {
		if (userService.existsByEmail(dto.email())) {
			throw new ExistEmailException(dto.email());
		}
		if (userService.existsByUsername(dto.username())) {
			throw new ExistUsernameException(dto.username());
		}

		Registration request = registrationRepository.findByEmail(dto.email())
				.orElseGet(() -> registrationMapper.toEntity(dto));

		request.updateCredentials(
				dto.username(),
				passwordEncoder.encode(dto.password())
		);

		String token = tokenGenerator.generate();
		while (registrationRepository.existsByToken(token)) {
			token = tokenGenerator.generate();
		}
		request.setToken(token);
		registrationRepository.save(request);
		notifier.sendVerificationToken(request.getEmail(), token);
		return registrationMapper.toResponse(request);
	}

	@Override
	public RegistrationResponse confirmAccount(String token) {
		Registration request = registrationRepository.findByToken(token).orElseThrow(TokenNotFoundException::new);
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
		registrationRepository.delete(request);

		return registrationMapper.toResponse(request);
	}

	@Override
	public RegistrationResponse resendToken(UUID registrationId) {
		Registration request = registrationRepository.findById(registrationId)
				.orElseThrow(() -> new RegistrationRequestNotFoundException(registrationId));
		String token = tokenGenerator.generate();
		while (registrationRepository.existsByToken(token)) {
			token = tokenGenerator.generate();
		}

		request.refreshToken(token);
		notifier.sendVerificationToken(request.getEmail(), token);
		return registrationMapper.toResponse(registrationRepository.save(request));
	}
}
