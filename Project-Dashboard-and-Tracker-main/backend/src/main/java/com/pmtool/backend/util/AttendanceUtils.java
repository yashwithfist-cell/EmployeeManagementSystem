package com.pmtool.backend.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.entity.Holiday;
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

	public static long calculateLeaveDays(List<LeaveDto> leaveDtoList, LocalDate start, LocalDate end,
			List<LocalDate> holidayList) {
		long leaveCount = 0;
		List<LocalDate> startDateList = leaveDtoList.stream().map(leave -> leave.getStartDate()).toList();
		List<LocalDate> endDateList = leaveDtoList.stream().map(leave -> leave.getEndDate()).toList();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			LocalDate loopDate = date;
			if (startDateList.contains(loopDate) || endDateList.contains(loopDate)) {
				throw new LeaveNotFoundException(
						"Duplicate leave dates present please check startdate and enddate !!!");
			}
			if (holidayList.contains(date))
				continue;

			DayOfWeek day = date.getDayOfWeek();
			// Skip Sundays
			if (day == DayOfWeek.SUNDAY)
				continue;

			// Mondays to Fridays: always count
			if (day != DayOfWeek.SATURDAY) {
				leaveCount++;
				continue;
			}

			// Saturdays: count only if consecutive leave started today or yesterday was
			// leave
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
		if (leaveCount == 0) {
			throw new LeaveNotAllowedException("Selected Dates are Holidays");
		}

			return leaveCount;
	}

}
