package com.pmtool.backend.mapper;

import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Leave;

public interface LeaveMapper {

	public LeaveDto convertToLeaveDto(Leave leave);
	public Leave convertToLeave(LeaveDto leaveDto,Employee employee);
}
