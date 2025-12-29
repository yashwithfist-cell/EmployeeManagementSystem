package com.pmtool.backend.mapper;

import com.pmtool.backend.DTO.SystemAttendanceDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SystemAttendance;

public interface SysteAttendanceMapper {
	public SystemAttendanceDto convertToSystemAttendanceDto(SystemAttendance systemAttendance);
	public SystemAttendance convertToSystemAttendance(SystemAttendanceDto systemAttendanceDto,Employee employee);
}
