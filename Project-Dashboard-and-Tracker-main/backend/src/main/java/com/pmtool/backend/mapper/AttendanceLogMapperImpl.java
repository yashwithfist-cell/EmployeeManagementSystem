package com.pmtool.backend.mapper;

import org.springframework.stereotype.Component;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.entity.AttendanceLog;
import com.pmtool.backend.enums.AttendanceStatus;
import com.pmtool.backend.util.AttendanceUtils;

@Component
public class AttendanceLogMapperImpl implements AttendanceLogMapper {

	@Override
	public AttendanceLogDto convertToAttendanceLogDto(AttendanceLog attendanceLog) {
		return AttendanceLogDto.builder().id(attendanceLog.getId()).empId(attendanceLog.getEmployee().getEmployeeId()).inTime(attendanceLog.getInTime())
				.outTime(attendanceLog.getOutTime()).totalHoursWorked(attendanceLog.getTotalHoursWorked())
				.totalHours(attendanceLog.getTotalHours()).status(attendanceLog.getStatus().name())
				.date(attendanceLog.getDate()).username(attendanceLog.getEmployee().getUsername()).build();
	}

	@Override
	public AttendanceLog convertToAttendanceLog(AttendanceLogDto attendanceLogDto) {
		return AttendanceLog.builder().id(attendanceLogDto.getId()).inTime(attendanceLogDto.getInTime())
				.outTime(attendanceLogDto.getOutTime()).totalHoursWorked(attendanceLogDto.getTotalHoursWorked())
				.totalHours(attendanceLogDto.getTotalHours())
				.status(AttendanceStatus.valueOf(attendanceLogDto.getStatus())).date(attendanceLogDto.getDate())
				.build();
	}

}
