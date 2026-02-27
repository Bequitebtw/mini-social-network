package com.bequitebtw.socialnetwork.common.builder;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.List;

@Component
public class ResponseBuilder {
	//Ok
	public <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, T data, String message, String instance) {
		return ResponseEntity
				.status(status)
				.body(ApiResponse.success(data, message, instance));
	}

	public <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, T data, String instance) {
		return ResponseEntity
				.status(status)
				.body(ApiResponse.success(data, instance));
	}

	public <T> ResponseEntity<ApiResponse<T>> ok(T data, String instance) {
		return success(HttpStatus.OK, data, instance);
	}

	public <T> ResponseEntity<ApiResponse<T>> ok(T data, String message, String instance) {
		return success(HttpStatus.OK, data, message, instance);
	}

	public <T> ResponseEntity<ApiResponse<T>> created(T data, String instance) {
		return success(HttpStatus.CREATED, data, instance);
	}

	public <T> ResponseEntity<ApiResponse<T>> created(T data, String message, String instance) {
		return success(HttpStatus.CREATED, data, message, instance);
	}

	public ResponseEntity<ApiResponse<Void>> noContent() {
		return ResponseEntity.noContent().build();
	}

	//Errors
	public ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message, String instance) {
		return ResponseEntity
				.status(status)
				.body(ApiResponse.error(message, instance));
	}

	public ResponseEntity<ApiResponse<Void>> error(HttpStatus status, BindException ex, String instance) {
		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		return ResponseEntity
				.status(status)
				.body(ApiResponse.error(ex.getMessage(), errors, instance));
	}

	public ResponseEntity<ApiResponse<Void>> badRequest(String message, String instance) {
		return error(HttpStatus.BAD_REQUEST, message, instance);
	}

	public ResponseEntity<ApiResponse<Void>> notFound(String message, String instance) {
		return error(HttpStatus.NOT_FOUND, message, instance);
	}

	public ResponseEntity<ApiResponse<Void>> conflict(String message, String instance) {
		return error(HttpStatus.CONFLICT, message, instance);
	}

	public ResponseEntity<ApiResponse<Void>> unauthorized(String message, String instance) {
		return error(HttpStatus.UNAUTHORIZED, message, instance);
	}

	public ResponseEntity<ApiResponse<Void>> serverError(String message, String instance) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, message, instance);
	}
}
