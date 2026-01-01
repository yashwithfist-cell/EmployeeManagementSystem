package com.pmtool.backend.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.entity.Holiday;
import com.pmtool.backend.enums.LeaveType;
import com.pmtool.backend.exception.LeaveNotAllowedException;
import com.pmtool.backend.exception.LeaveNotFoundException;

public class AttendanceUtils {
	public static LocalDateTime getFirstCheckIn(List<AttendanceLogDto> logs) {
		return logs.stream().filter(l -> "in".equalsIgnoreCase(l.getDirection())).map(AttendanceLogDto::getTimestamp)
				.min(LocalDateTime::compareTo).orElse(null);
	}

	public static LocalDateTime getLastCheckOut(List<AttendanceLogDto> logs) {
		return logs.stream().filter(l -> "out".equalsIgnoreCase(l.getDirection())).map(AttendanceLogDto::getTimestamp)
				.max(LocalDateTime::compareTo).orElse(null);
	}

	public static String formatDuration(Duration duration) {
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static long calculateLeaveDays(List<LeaveDto> leaveDtoList, LeaveDto leaveDto, List<LocalDate> holidayList) {
		LocalDate start = leaveDto.getStartDate();
		LocalDate end = leaveDto.getEndDate();
		long leaveCount = 0;
		Set<LocalDate> startDateSet = leaveDtoList.stream()
		        .map(LeaveDto::getStartDate)
		        .collect(Collectors.toSet());

		Set<LocalDate> endDateSet = leaveDtoList.stream()
				.map(LeaveDto::getEndDate).collect(Collectors.toSet());
		if (leaveDto.getLeaveType().equals(LeaveType.EARNED)) {
			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
				leaveCount++;
			}
		} else {
			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
				LocalDate loopDate = date;
				if (startDateSet.contains(loopDate) || endDateSet.contains(loopDate)) {
					throw new LeaveNotFoundException(
							"Duplicate leave dates present please check startdate and enddate !!!");
				}
				if (holidayList.contains(date))
					continue;

				DayOfWeek day = date.getDayOfWeek();
				if (day == DayOfWeek.SUNDAY)
					continue;

				if (day != DayOfWeek.SATURDAY) {
					leaveCount++;
					continue;
				}

				if (day == DayOfWeek.SATURDAY) {
					int dayOfMonth = date.getDayOfMonth();
					int saturdayNumber = ((dayOfMonth - 1) / 7) + 1;
					if (saturdayNumber == 2 || saturdayNumber == 4) {
						continue;
					} else {
						leaveCount++;
					}
				}
			}
		}

		if (leaveCount == 0) {
			throw new LeaveNotAllowedException("Selected Dates are Holidays");
		}

		return leaveCount;
	}

}
