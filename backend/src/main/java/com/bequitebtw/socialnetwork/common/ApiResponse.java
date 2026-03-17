package com.bequitebtw.socialnetwork.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private List<String> errors;
	private String instance;
	private LocalDateTime timestamp;

	public static <T> ApiResponse<T> success(T data, String instance) {
		return ApiResponse.<T>builder()
				.success(true)
				.data(data)
				.instance(instance)
				.timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> success(T data, String message, String instance) {
		return ApiResponse.<T>builder()
				.success(true)
				.message(message)
				.data(data)
				.instance(instance)
				.timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> error(String message, String instance) {
		return ApiResponse.<T>builder()
				.success(false)
				.message(message)
				.instance(instance)
				.timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> error(String message, List<String> errors, String instance) {
		return ApiResponse.<T>builder()
				.success(false)
				.message(message)
				.errors(errors)
				.instance(instance)
				.timestamp(LocalDateTime.now())
				.build();
	}
}