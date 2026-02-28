package com.bequitebtw.socialnetwork.common.exception;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
	private final ResponseBuilder responseBuilder;

	@ExceptionHandler(ExistUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleUsernameExistException(ExistUsernameException ex, HttpServletRequest request) {
		return responseBuilder.conflict(ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ExistEmailException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailExistException(ExistEmailException ex, HttpServletRequest request) {
		return responseBuilder.conflict(ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ExpiredRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleRequestExpiredException(ExpiredRequestException ex, HttpServletRequest request) {
		return responseBuilder.unauthorized(ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		return responseBuilder.error(HttpStatus.BAD_REQUEST, ex, request.getRequestURI());
	}

	@ExceptionHandler(RegistrationRequestNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleRegistrationRequestNotFoundException(RegistrationRequestNotFoundException ex, HttpServletRequest request) {
		return responseBuilder.notFound(ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(BadCredentialsAuthenticationException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentialsAuthenticationException(BadCredentialsAuthenticationException ex, HttpServletRequest request) {
		return responseBuilder.unauthorized(ex.getMessage(), request.getRequestURI());
	}

	//others
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
		return responseBuilder.serverError(ex.getMessage(), request.getRequestURI());
	}
}