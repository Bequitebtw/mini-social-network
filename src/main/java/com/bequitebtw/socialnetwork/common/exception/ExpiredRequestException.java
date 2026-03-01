package com.bequitebtw.socialnetwork.common.exception;


public class ExpiredRequestException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Registration request was expired";
	}
}
