package com.pmtool.backend.DTO.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
	private boolean success;
	private String message;
	private Object data;
	private String errorCode;
	private LocalDateTime timestamp;
	private String path;

	public static ApiResponse success(Object data, String message, String path) {
		return ApiResponse.builder().success(true).message(message).data(data).timestamp(LocalDateTime.now()).path(path)
				.build();
	}

	public static ApiResponse failure(String message, String errorCode, String path) {
		return ApiResponse.builder().success(false).message(message).errorCode(errorCode).timestamp(LocalDateTime.now())
				.path(path).build();
	}
}
