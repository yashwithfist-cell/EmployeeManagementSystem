package com.pmtool.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_attendance")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemAttendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@Column(name = "login_date")
	private LocalDate date;
	@Column(name = "login")
	private LocalDateTime startTime;
	@Column(name = "logout")
	private LocalDateTime endTime;
	@Column(name = "total_work_hours")
	private Long totalWorkMs = 0L;
	@Column(name = "total_break_hours")
	private Long totalBreakMs = 0L;
	@Column(name = "break_start_time")
	private LocalDateTime breakStartTime;
	@Column(name = "total_meeting_timings")
	private Long totalMeetingMs = 0L;
	@Column(name = "total_event_timings")
	private Long totalEventMs = 0L;
	@Column(name = "total_training_timings")
	private Long totalTrainingMs = 0L;
}
