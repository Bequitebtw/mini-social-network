package com.bequitebtw.socialnetwork.domain.authentication.controller;


import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationResponse;
import com.bequitebtw.socialnetwork.domain.authentication.service.AuthenticationService;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
	private final ResponseBuilder responseBuilder;
	private final AuthenticationService authenticationService;

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request) throws Exception {
		log.info("Запрос на вход под логином: {}", authenticationRequest.login());
		return responseBuilder.ok(authenticationService.authenticate(authenticationRequest), request.getRequestURI());
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return responseBuilder.noContent();
	}

}