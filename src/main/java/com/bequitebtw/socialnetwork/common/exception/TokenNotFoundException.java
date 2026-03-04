package com.bequitebtw.socialnetwork.common.exception;


public class TokenNotFoundException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Refresh token not found";
	}
}
