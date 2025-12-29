package com.pmtool.backend.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
	private Long id;
	private String username;
	private String title;
	private String message;
	private boolean readStatus = false;
	private LocalDateTime createdAt = LocalDateTime.now();
}
