package com.pmtool.backend.mapper;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.entity.AttendanceLog;

public interface AttendanceLogMapper {
	public AttendanceLogDto convertToAttendanceLogDto(AttendanceLog attendanceLog);
	public AttendanceLog convertToAttendanceLog(AttendanceLogDto attendanceLogDto);
}
