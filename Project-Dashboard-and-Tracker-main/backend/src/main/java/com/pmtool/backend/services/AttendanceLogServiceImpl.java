package com.pmtool.backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.mapper.AttendanceLogMapper;
import com.pmtool.backend.repository.AttendanceRepository;

@Service
public class AttendanceLogServiceImpl implements AttendanceLogService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private AttendanceLogMapper attendanceLogMapper;

	public List<AttendanceLogDto> searchAttendance(LocalDate fromDate, LocalDate toDate) {
		return attendanceRepository.getAttendanceBetweenDates(fromDate, toDate).stream()
				.map(log -> attendanceLogMapper.convertToAttendanceLogDto(log)).toList();
	}

	@Override
	public List<AttendanceLogDto> searchAttendanceByEmployee(LocalDate fromDate, LocalDate toDate, String username) {
		return attendanceRepository.getAttendanceByEmpBetweenDates(fromDate, toDate, username).stream()
				.map(log -> attendanceLogMapper.convertToAttendanceLogDto(log)).toList();
	}

}
