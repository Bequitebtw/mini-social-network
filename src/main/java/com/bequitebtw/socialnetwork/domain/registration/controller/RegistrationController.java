package com.bequitebtw.socialnetwork.domain.registration.controller;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationDto;
import com.bequitebtw.socialnetwork.domain.registration.dto.UserRegistrationResponse;
import com.bequitebtw.socialnetwork.domain.registration.service.UserRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegistrationController {
	private final UserRegistrationService userRegistrationService;
	private final ResponseBuilder responseBuilder;

	@PostMapping("/registration")
	public ResponseEntity<ApiResponse<UserRegistrationResponse>> createRegistrationRequest(@RequestBody @Valid UserRegistrationDto userRegistrationDto, HttpServletRequest request) {
		return responseBuilder.created(userRegistrationService.createUserRequest(userRegistrationDto), "Сообщение с подтверждением отправлено на вашу почту!", request.getRequestURI());
	}

	@GetMapping("/confirm")
	public ResponseEntity<ApiResponse<UserRegistrationResponse>> confirmAccount(@RequestParam("token") String tokenValue, HttpServletRequest request) {
		return responseBuilder.ok(userRegistrationService.confirmAccount(tokenValue), "Аккаунт подтвержден!", request.getRequestURI());
	}

	@PostMapping("/{registrationId}/resend")
	public ResponseEntity<ApiResponse<UserRegistrationResponse>> resendToken(@PathVariable UUID registrationId, HttpServletRequest request) {
		return responseBuilder.ok(userRegistrationService.resendToken(registrationId), "Повтороное сообщение с подтверждение отправлено на вашу почту!", request.getRequestURI());
	}
}