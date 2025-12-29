package com.pmtool.backend.mapper;

import org.springframework.stereotype.Component;

import com.pmtool.backend.DTO.SystemAttendanceDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SystemAttendance;

@Component
public class SystemAttendanceMapperImpl implements SysteAttendanceMapper {

	@Override
	public SystemAttendanceDto convertToSystemAttendanceDto(SystemAttendance systemAttendance) {
		return SystemAttendanceDto.builder().date(systemAttendance.getDate()).id(systemAttendance.getId())
				.startTime(systemAttendance.getStartTime()).endTime(systemAttendance.getEndTime())
				.totalBreakMs(systemAttendance.getTotalBreakMs()).totalWorkMs(systemAttendance.getTotalWorkMs())
				.username(systemAttendance.getEmployee().getUsername())
				.totalMeetingMs(systemAttendance.getTotalMeetingMs()).totalEventMs(systemAttendance.getTotalEventMs())
				.totalTrainingMs(systemAttendance.getTotalTrainingMs()).build();
	}

	@Override
	public SystemAttendance convertToSystemAttendance(SystemAttendanceDto systemAttendanceDto, Employee employee) {
		return SystemAttendance.builder().date(systemAttendanceDto.getDate()).id(systemAttendanceDto.getId())
				.startTime(systemAttendanceDto.getStartTime()).endTime(systemAttendanceDto.getEndTime())
				.totalBreakMs(systemAttendanceDto.getTotalBreakMs()).totalWorkMs(systemAttendanceDto.getTotalWorkMs())
				.totalMeetingMs(systemAttendanceDto.getTotalMeetingMs())
				.totalEventMs(systemAttendanceDto.getTotalEventMs())
				.totalTrainingMs(systemAttendanceDto.getTotalTrainingMs()).build();
	}

}
