package com.pmtool.backend.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDto {
	private Long id;
	private String holidayName;
	private LocalDate holidayDate;
}
