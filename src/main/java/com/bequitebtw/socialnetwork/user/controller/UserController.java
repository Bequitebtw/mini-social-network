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


	@GetMapping("/{username}/profile")
	public ResponseEntity<ApiResponse<UserShort>> getUserProfile(@PathVariable String username, HttpServletRequest request) {
		UserShort user = userService.findUserByUsername(username);
		return ResponseBuilder.ok()
				.data(user)
				.instance(request.getRequestURI())
				.build();
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserShort>> getMyProfile(@AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal, HttpServletRequest request) {
		UserShort user = userService.findUserById(jwtUserPrincipal.getId());
		return ResponseBuilder.ok().data(user).instance(request.getRequestURI()).build();
	}

	@GetMapping("/test")
	public ResponseEntity<ApiResponse<String>> test(HttpServletRequest request) {
		return ResponseBuilder.ok().message("Ваш токен работает успешно!").instance(request.getRequestURI()).build();
	}
}
