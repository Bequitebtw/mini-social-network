package com.bequitebtw.socialnetwork.common.builder;

import com.bequitebtw.socialnetwork.common.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;

import java.net.URI;
import java.util.List;

public class ResponseBuilder {

	public static BodyBuilder ok() {
		return new BodyBuilder(HttpStatus.OK);
	}

	public static BodyBuilder found() {
		return new BodyBuilder(HttpStatus.FOUND);
	}

	public static BodyBuilder created() {
		return new BodyBuilder(HttpStatus.CREATED);
	}

	public static ErrorBuilder badRequest() {
		return new ErrorBuilder(HttpStatus.BAD_REQUEST);
	}

	public static ErrorBuilder notFound() {
		return new ErrorBuilder(HttpStatus.NOT_FOUND);
	}

	public static ErrorBuilder conflict() {
		return new ErrorBuilder(HttpStatus.CONFLICT);
	}

	public static ErrorBuilder unauthorized() {
		return new ErrorBuilder(HttpStatus.UNAUTHORIZED);
	}

	public static ErrorBuilder serverError() {
		return new ErrorBuilder(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static ResponseEntity<ApiResponse<Void>> noContent() {
		return ResponseEntity.noContent().build();
	}

	public static abstract class BaseBuilder<T extends BaseBuilder<T>> {
		protected final HttpStatus status;
		protected final HttpHeaders headers = new HttpHeaders();
		protected URI location;
		protected String instance;
		protected String message;
		protected List<String> errors;

		protected BaseBuilder(HttpStatus status) {
			this.status = status;
		}

		@SuppressWarnings("unchecked")
		public T header(String headerName, String headerValue) {
			headers.add(headerName, headerValue);
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T cookie(ResponseCookie cookie) {
			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T location(URI location) {
			this.location = location;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T instance(String instance) {
			this.instance = instance;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T message(String message) {
			this.message = message;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T errors(List<String> errors) {
			this.errors = errors;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T errorsFromBindException(BindException ex) {
			this.errors = ex.getBindingResult()
					.getFieldErrors()
					.stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage())
					.toList();
			this.message = ex.getMessage();
			return (T) this;
		}
	}

	public static class BodyBuilder extends BaseBuilder<BodyBuilder> {

		private Object data;

		public BodyBuilder(HttpStatus status) {
			super(status);
		}

		public BodyBuilder data(Object data) {
			this.data = data;
			return this;
		}

		public <T> ResponseEntity<ApiResponse<T>> build() {
			ApiResponse<T> apiResponse = ApiResponse.success((T) data, message, instance);
			ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(status).headers(headers);
			if (location != null) {
				responseBuilder.location(location);
			}
			return responseBuilder.body(apiResponse);
		}
	}

	public static class ErrorBuilder extends BaseBuilder<ErrorBuilder> {

		public ErrorBuilder(HttpStatus status) {
			super(status);
		}

		public ResponseEntity<ApiResponse<Void>> build() {
			ApiResponse<Void> apiResponse;
			if (errors != null) {
				apiResponse = ApiResponse.error(message, errors, instance);
			} else {
				apiResponse = ApiResponse.error(message, instance);
			}
			ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(status).headers(headers);
			if (location != null) {
				responseBuilder.location(location);
			}
			return responseBuilder.body(apiResponse);
		}
	}
}