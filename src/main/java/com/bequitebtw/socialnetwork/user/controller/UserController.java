package com.bequitebtw.socialnetwork.user.controller;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;
import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.user.service.UserService;
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
	public ResponseEntity<ApiResponse<UserShort>> getUserProfile(@PathVariable String username, HttpServletRequest request) {
		return responseBuilder.ok(userService.findUserByUsername(username), request.getRequestURI());
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserShort>> getMyProfile(@AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal, HttpServletRequest request) {
		return responseBuilder.ok(userService.findUserById(jwtUserPrincipal.getId()), request.getRequestURI());
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Ваш токен работает!!!");
	}
}
