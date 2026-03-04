package com.bequitebtw.socialnetwork.common.exception;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

	@ExceptionHandler(ExistUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleUsernameExistException(ExistUsernameException ex, HttpServletRequest request) {
		return ResponseBuilder
				.conflict()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(ExistEmailException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailExistException(ExistEmailException ex, HttpServletRequest request) {
		return ResponseBuilder
				.conflict()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(ExpiredRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleRequestExpiredException(ExpiredRequestException ex, HttpServletRequest request) {
		return ResponseBuilder
				.unauthorized()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(ExpiredRefreshTokenException.class)
	public ResponseEntity<ApiResponse<Void>> handleExpiredRefreshTokenException(ExpiredRefreshTokenException ex, HttpServletRequest request) {
		return ResponseBuilder
				.unauthorized()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		return ResponseBuilder
				.badRequest()
				.errors(errors)
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(RegistrationRequestNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleRegistrationRequestNotFoundException(RegistrationRequestNotFoundException ex, HttpServletRequest request) {
		return ResponseBuilder
				.notFound()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(LoginNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleLoginNotFoundException(LoginNotFoundException ex, HttpServletRequest request) {
		return ResponseBuilder
				.unauthorized()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(TokenNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleRefreshTokenNotFoundException(TokenNotFoundException ex, HttpServletRequest request) {
		return ResponseBuilder
				.notFound()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
		return ResponseBuilder
				.notFound()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
		return ResponseBuilder
				.unauthorized()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}
	//others
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
		return ResponseBuilder
				.serverError()
				.message(ex.getMessage())
				.instance(request.getRequestURI())
				.build();
	}
}