package com.bequitebtw.socialnetwork.domain.registration.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthorizationPageController {

	@GetMapping("/register")
	public String showRegisterPage() {
		return "register";
	}
}
