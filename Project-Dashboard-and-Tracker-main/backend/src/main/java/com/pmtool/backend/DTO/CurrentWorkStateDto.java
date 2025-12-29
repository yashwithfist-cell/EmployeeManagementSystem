package com.pmtool.backend.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWorkStateDto {
	 private boolean isWorking;
	    private boolean isOnBreak;
	    private LocalDateTime startTime;
	    private long totalBreakMs;
}
