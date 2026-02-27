package com.bequitebtw.socialnetwork.domain.authentication.controller;


import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
	private final ResponseBuilder responseBuilder;

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login() {
		return responseBuilder.noContent();
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return responseBuilder.noContent();
	}

}