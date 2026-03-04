package com.bequitebtw.socialnetwork.registration.controller;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationRequest;
import com.bequitebtw.socialnetwork.registration.dto.RegistrationResponse;
import com.bequitebtw.socialnetwork.registration.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class RegistrationController {
	private final RegistrationService registrationService;

	@PostMapping("/registration")
	public ResponseEntity<ApiResponse<RegistrationResponse>> createRegistrationRequest(@RequestBody @Valid RegistrationRequest registrationRequest, HttpServletRequest request) {
		log.info("Запрос на регистрацию username: {},email: {}", registrationRequest.username(), registrationRequest.email());
		RegistrationResponse registrationResponse = registrationService.createRegistrationRequest(registrationRequest);

		return ResponseBuilder.created()
				.data(registrationResponse)
				.message("Сообщение с подтверждением отправлено на вашу почту!")
				.instance(request.getRequestURI())
				.build();
	}

	@GetMapping("/confirm")
	public ResponseEntity<ApiResponse<RegistrationResponse>> confirmAccount(@RequestParam("token") String tokenValue, HttpServletRequest request) {
		log.info("Заявка на подтверждение токена: {}", tokenValue);
		RegistrationResponse registrationResponse = registrationService.confirmAccount(tokenValue);

		return ResponseBuilder.found()
				.data(registrationResponse)
				.message("Аккаунт подтвержден!")
				.instance(request.getRequestURI())
				.location(URI.create("/login"))
				.build();
	}

	@PostMapping("/{registrationId}/resend")
	public ResponseEntity<ApiResponse<RegistrationResponse>> resendToken(@PathVariable UUID registrationId, HttpServletRequest request) {
		log.info("Пользователь {} отправил повторный запрос на отправку токена", registrationId);
		RegistrationResponse registrationResponse = registrationService.resendToken(registrationId);
		return ResponseBuilder.found()
				.data(registrationResponse)
				.message("Повтороное сообщение с подтверждение отправлено на вашу почту!")
				.instance(request.getRequestURI())
				.build();
	}
}