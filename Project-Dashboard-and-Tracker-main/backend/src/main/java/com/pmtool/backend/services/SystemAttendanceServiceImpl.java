package com.pmtool.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.CurrentWorkStateDto;
import com.pmtool.backend.DTO.SystemAttendanceDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SystemAttendance;
import com.pmtool.backend.entity.Training;
import com.pmtool.backend.mapper.SysteAttendanceMapper;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.SystemAttendanceRepository;
import com.pmtool.backend.repository.TrainingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAttendanceServiceImpl implements SystemAttendanceService {

	private final SystemAttendanceRepository repo;
	private final EmployeeRepository empRepo;
	private final SysteAttendanceMapper mapper;
	private final TrainingRepository trainingRepo;

	@Override
	public SystemAttendanceDto startWork(Map<String, String> body) {
		Employee emp = empRepo.findByUsername(body.get("username"))
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		SystemAttendance old = repo.findByDateAndEmployee(emp.getUsername(), LocalDate.now());
		SystemAttendance attendance = null;
		if (old != null) {
			attendance = SystemAttendance.builder().employee(emp).id(old.getId()).date(old.getDate())
					.startTime(old.getStartTime()).totalBreakMs(old.getTotalBreakMs()).totalWorkMs(old.getTotalWorkMs())
					.totalMeetingMs(old.getTotalMeetingMs()).totalEventMs(old.getTotalEventMs())
					.totalTrainingMs(old.getTotalTrainingMs()).build();
		} else {
			attendance = SystemAttendance.builder().employee(emp).date(LocalDate.now()).startTime(LocalDateTime.now())
					.totalBreakMs(0l).totalWorkMs(0l).totalMeetingMs(0l).totalEventMs(0l).totalTrainingMs(0l).build();
		}
		return mapper.convertToSystemAttendanceDto(repo.save(attendance));
	}

	@Override
	public SystemAttendanceDto startBreak(Map<String, String> body) {
		Employee emp = empRepo.findByUsername(body.get("username"))
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		SystemAttendance att = repo.findByDateAndEmployee(emp.getUsername(), LocalDate.now());
		att.setBreakStartTime(LocalDateTime.now());
		return mapper.convertToSystemAttendanceDto(repo.save(att));
	}

	@Override
	public SystemAttendanceDto stopWork(Map<String, Object> body) {
		Employee emp = empRepo.findByUsername((String) body.get("username"))
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		SystemAttendance att = repo.findByDateAndEmployee(emp.getUsername(), LocalDate.now());
		att.setEndTime(LocalDateTime.now());
		att.setTotalWorkMs(((Number) body.get("totalWorkMs")).longValue() + att.getTotalWorkMs());
		att.setTotalBreakMs(((Number) body.get("totalBreakMs")).longValue() + att.getTotalBreakMs());
		att.setTotalMeetingMs(((Number) body.get("totalMeetingMs")).longValue() + att.getTotalMeetingMs());
		att.setTotalEventMs(((Number) body.get("totalEventMs")).longValue() + att.getTotalEventMs());
		att.setTotalTrainingMs(((Number) body.get("totalTrainingMs")).longValue() + att.getTotalTrainingMs());
		return mapper.convertToSystemAttendanceDto(repo.save(att));
	}

	@Override
	public List<SystemAttendanceDto> getAllAttendance(String fromDate, String toDate, String username) {
		return (username == null || username.isEmpty() || username.isBlank())
				? repo.findAllByDate(LocalDate.parse(fromDate), LocalDate.parse(toDate)).stream()
						.map(entity -> mapper.convertToSystemAttendanceDto(entity)).toList()
				: repo.findAllByDate(LocalDate.parse(fromDate), LocalDate.parse(toDate), username).stream()
						.map(entity -> mapper.convertToSystemAttendanceDto(entity)).toList();
	}

	@Override
	public SystemAttendanceDto stopBreak(Map<String, String> body) {
		Employee emp = empRepo.findByUsername(body.get("username"))
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		SystemAttendance att = repo.findByDateAndEmployee(emp.getUsername(), LocalDate.now());
		if (att.getBreakStartTime() != null) {
			long breakDuration = Duration.between(att.getBreakStartTime(), LocalDateTime.now()).toMillis();
			att.setTotalBreakMs(att.getTotalBreakMs() + breakDuration);
			att.setBreakStartTime(null);
		}
		return mapper.convertToSystemAttendanceDto(repo.save(att));
	}

	@Override
	public List<SystemAttendanceDto> getAllAttendanceByUser(Authentication authentication) {
		return repo.findAllByUser(authentication.getName()).stream()
				.map(entity -> mapper.convertToSystemAttendanceDto(entity)).toList();
	}

	@Override
	public CurrentWorkStateDto getCurrentWorkState(Authentication authentication) {
		String username = authentication.getName();

		Employee emp = empRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Employee Not Found !"));

		SystemAttendance att = repo.findByDateAndEmployee(emp.getUsername(), LocalDate.now());

		// No attendance today → not working
		if (att == null || att.getStartTime() == null) {
			return new CurrentWorkStateDto(false, false, null, 0L);
		}

		boolean isOnBreak = att.getBreakStartTime() != null;
		long totalBreakMs = att.getTotalBreakMs();

		return new CurrentWorkStateDto(true, isOnBreak, att.getStartTime(), totalBreakMs);
	}

	@Override
	public void addTraining(String startTime, String stopTime, String description, Authentication authentication) {
		long startSec = Long.parseLong(startTime);
		long endSec = Long.parseLong(stopTime);
		LocalDateTime startTrTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startSec), ZoneId.systemDefault());
		LocalDateTime endTrTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endSec), ZoneId.systemDefault());
		Employee emp = empRepo.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		trainingRepo.save(Training.builder().startTime(startTrTime).endTime(endTrTime).description(description)
				.employee(emp).build());
	}

}
