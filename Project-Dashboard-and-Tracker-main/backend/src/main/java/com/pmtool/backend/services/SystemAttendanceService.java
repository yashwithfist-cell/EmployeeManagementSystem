package com.pmtool.backend.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.CurrentWorkStateDto;
import com.pmtool.backend.DTO.SystemAttendanceDto;

public interface SystemAttendanceService {
	public SystemAttendanceDto startWork(Map<String, String> body);

	public SystemAttendanceDto startBreak(Map<String, String> body);

	public SystemAttendanceDto stopBreak(Map<String, String> body);

	public SystemAttendanceDto stopWork(Map<String, Object> body);

	public List<SystemAttendanceDto> getAllAttendance(String fromDate, String toDate, String username);

	public List<SystemAttendanceDto> getAllAttendanceByUser(Authentication authentication);

	CurrentWorkStateDto getCurrentWorkState(Authentication authentication);

	public void addTraining(String startTime, String stopTime, String description, Authentication authentication);

}
