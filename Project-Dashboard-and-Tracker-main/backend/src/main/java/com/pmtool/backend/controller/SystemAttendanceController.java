package com.pmtool.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.CurrentWorkStateDto;
import com.pmtool.backend.DTO.SystemAttendanceDto;
import com.pmtool.backend.services.SystemAttendanceService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/systemattendance")
@RequiredArgsConstructor
@CrossOrigin
public class SystemAttendanceController {
	private final SystemAttendanceService service;

	@PostMapping("/start")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public SystemAttendanceDto startWork(@RequestBody Map<String, String> body) {
		return service.startWork(body);
	}

	@PostMapping("/break/start")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public SystemAttendanceDto startBreak(@RequestBody Map<String, String> body) {
		return service.startBreak(body);
	}

	@PostMapping("/break/stop")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public SystemAttendanceDto stopBreak(@RequestBody Map<String, String> body) {
		return service.stopBreak(body);
	}

	@PostMapping("/stop")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public SystemAttendanceDto stopWork(@RequestBody Map<String, Object> body) {
		return service.stopWork(body);
	}

	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public List<SystemAttendanceDto> getAllAttendance(@RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam String username) {
		return service.getAllAttendance(fromDate, toDate, username);
	}

	@GetMapping("/getEmpSysLog")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public List<SystemAttendanceDto> getAllAttendanceByUser(Authentication authentication) {
		return service.getAllAttendanceByUser(authentication);
	}

	@GetMapping("/currentWorkState")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public CurrentWorkStateDto getCurrentWorkState(Authentication authentication) {
		return service.getCurrentWorkState(authentication);
	}

	@PostMapping("/training")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public void addTraining(@RequestParam String startTrainingTime, @RequestParam String endTrainingTime,
			@RequestParam String trainingDesc, Authentication authentication) {
		service.addTraining(startTrainingTime, endTrainingTime, trainingDesc, authentication);
	}

}
