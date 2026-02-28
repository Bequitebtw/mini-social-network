package com.bequitebtw.socialnetwork.common.exception;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadCredentialsAuthenticationException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Credentials are not valid";
	}

}
