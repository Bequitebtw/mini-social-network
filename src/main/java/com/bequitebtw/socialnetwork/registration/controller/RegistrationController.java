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
import org.springframework.http.HttpStatus;
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
	private final ResponseBuilder responseBuilder;

	@PostMapping("/registration")
	public ResponseEntity<ApiResponse<RegistrationResponse>> createRegistrationRequest(@RequestBody @Valid RegistrationRequest registrationRequest, HttpServletRequest request) {
		log.info("Запрос на регистрацию username: {},email: {}", registrationRequest.username(), registrationRequest.email());
		return responseBuilder.created(registrationService.createRegistrationRequest(registrationRequest), "Сообщение с подтверждением отправлено на вашу почту!", request.getRequestURI());
	}

	@GetMapping("/confirm")
	public ResponseEntity<ApiResponse<RegistrationResponse>> confirmAccount(@RequestParam("token") String tokenValue, HttpServletRequest request) {
		log.info("Заявка на подтверждение токена: {}", tokenValue);
		return responseBuilder.success(HttpStatus.FOUND, registrationService.confirmAccount(tokenValue), "Аккаунт подтвержден!", request.getRequestURI(), URI.create("/api/users/profile"));
	}

	@PostMapping("/{registrationId}/resend")
	public ResponseEntity<ApiResponse<RegistrationResponse>> resendToken(@PathVariable UUID registrationId, HttpServletRequest request) {
		log.info("Пользователь {} отправил повторный запрос на отправку токена", registrationId);
		return responseBuilder.ok(registrationService.resendToken(registrationId), "Повтороное сообщение с подтверждение отправлено на вашу почту!", request.getRequestURI());
	}
}