package com.bequitebtw.socialnetwork.domain.user.controller;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.domain.user.model.User;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import com.bequitebtw.socialnetwork.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final ResponseBuilder responseBuilder;


	@GetMapping("/{username}/profile")
	public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable String username, HttpServletRequest request) {
		return responseBuilder.ok(userService.findUserProfileByUsername(username), request.getRequestURI());
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<User>> getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletRequest request) {
		return responseBuilder.ok(userService.findUserProfileByUsername(userPrincipal.getUsername()), request.getRequestURI());
	}
}
