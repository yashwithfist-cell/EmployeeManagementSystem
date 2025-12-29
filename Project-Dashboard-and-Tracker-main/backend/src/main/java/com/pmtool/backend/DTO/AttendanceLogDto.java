package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceLogDto {

	private Long id;
	private String empId;
	private LocalDateTime timestamp;
	private String direction;
	private LocalDate date;
	private LocalDateTime inTime;
	private LocalDateTime outTime;
	private String totalHours;
	private String totalHoursWorked;
	private String status;
	private String username;
	
	public AttendanceLogDto(String empId, LocalDateTime timestamp, String direction) {
		this.empId = empId;
		this.timestamp = timestamp;
		this.direction = direction;
	}

//	private Employee employee;

}
