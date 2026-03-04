package com.bequitebtw.socialnetwork.common.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
	@Override
	public String getMessage() {
		return "Refresh token was expired";
	}
}
