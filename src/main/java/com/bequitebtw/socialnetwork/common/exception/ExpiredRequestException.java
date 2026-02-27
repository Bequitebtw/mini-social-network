package com.bequitebtw.socialnetwork.common.exception;

import lombok.Getter;

@Getter
public class ExpiredRequestException extends RuntimeException {

	@Override
	public String getMessage() {
		return "Registration request was expired";
	}
}
