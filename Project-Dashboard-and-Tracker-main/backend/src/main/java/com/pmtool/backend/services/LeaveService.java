package com.pmtool.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.EmployeeDTO;
import com.pmtool.backend.DTO.HolidayDto;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.enums.Role;


public interface LeaveService {
	 public LeaveDto applyLeave(LeaveDto leave,Authentication authentication);
	 public List<LeaveDto> getAllLeaves();
	 public List<LeaveDto> getLeavesByEmployee(Authentication authentication);
	 public LeaveDto updateStatus(Long id, String status);
	 public List<String> getEmployeesByRole(Role projectManager,Authentication authentication);
	 public List<LeaveDto> getAllByManager(Authentication authentication);
	 public List<LeaveDto> getAllByTeamLead(Authentication authentication);
	 public List<HolidayDto> getAllHolidays();
}
