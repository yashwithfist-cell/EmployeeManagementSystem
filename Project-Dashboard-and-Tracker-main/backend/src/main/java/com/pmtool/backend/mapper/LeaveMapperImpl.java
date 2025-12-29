package com.pmtool.backend.mapper;

import org.springframework.stereotype.Component;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Leave;
import com.pmtool.backend.enums.LeaveStatus;
import com.pmtool.backend.util.AttendanceUtils;

@Component
public class LeaveMapperImpl implements LeaveMapper {

	@Override
	public LeaveDto convertToLeaveDto(Leave leaveResponse) {
		return LeaveDto.builder().id(leaveResponse.getId()).employeeId(leaveResponse.getEmployee().getEmployeeId())
				.username(leaveResponse.getEmployee().getUsername()).id(leaveResponse.getId())
				.startDate(leaveResponse.getStartDate()).endDate(leaveResponse.getEndDate())
				.leaveType(leaveResponse.getLeaveType()).status(leaveResponse.getStatus())
				.reason(leaveResponse.getReason()).projectManagerName(leaveResponse.getProjectManagerName()).days(leaveResponse.getDays()).unPaidLeave(leaveResponse.getUnPaidLeave()).teamLeadName(leaveResponse.getTeamLeadName()).build();
	}

	@Override
	public Leave convertToLeave(LeaveDto leaveDto, Employee employee) {
		return Leave.builder().id(leaveDto.getId()).startDate(leaveDto.getStartDate()).endDate(leaveDto.getEndDate())
				.reason(leaveDto.getReason()).leaveType(leaveDto.getLeaveType()).status(leaveDto.getStatus())
				.projectManagerName(leaveDto.getProjectManagerName()).employee(employee).days(leaveDto.getDays()).unPaidLeave(leaveDto.getUnPaidLeave()).teamLeadName(leaveDto.getTeamLeadName()).build();
	}

}
