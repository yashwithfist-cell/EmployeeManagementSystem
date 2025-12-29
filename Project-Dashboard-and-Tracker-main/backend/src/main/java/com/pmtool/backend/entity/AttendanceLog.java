package com.pmtool.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pmtool.backend.enums.AttendanceStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance_log")
public class AttendanceLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "punch_date")
	private LocalDate date;

	@Column(name = "in_time")
	private LocalDateTime inTime;

	@Column(name = "out_time")
	private LocalDateTime outTime;

	@Column(name = "total_hours")
	private String totalHours;
	
	@Column(name = "total_hours_worked")
	private String totalHoursWorked;
	
	@Enumerated(EnumType.STRING)
	private AttendanceStatus status;

	// 🔹 Many attendance logs belong to one employee
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

}
