package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemAttendanceDto {
	private Long id;
	private LocalDate date;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Long totalWorkMs;
	private Long totalBreakMs;
	private LocalDateTime breakStartTime;
	private String username;
	private Long totalMeetingMs;
    private Long totalEventMs;
    private Long totalTrainingMs;
}
