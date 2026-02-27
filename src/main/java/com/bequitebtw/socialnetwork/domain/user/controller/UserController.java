package com.bequitebtw.socialnetwork.domain.user.controller;

import com.bequitebtw.socialnetwork.domain.user.model.User;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import com.bequitebtw.socialnetwork.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
	public User getUserProfile(@PathVariable String username) {
		return userService.findUserProfileByUsername(username);
	}

	@GetMapping("/profile")
	public User getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		System.out.println(userPrincipal.getUsername());
		return userService.findUserProfileByUsername(userPrincipal.getUsername());
	}
}
