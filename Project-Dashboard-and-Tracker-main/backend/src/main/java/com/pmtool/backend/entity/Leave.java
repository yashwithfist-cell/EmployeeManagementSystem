package com.pmtool.backend.entity;

import java.time.LocalDate;

import com.pmtool.backend.enums.LeaveStatus;
import com.pmtool.backend.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "leave_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
	private Employee employee;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;
	@Column(name = "reason", nullable = false)
	private String reason;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private LeaveStatus status = LeaveStatus.PENDING;
	@Enumerated(EnumType.STRING)
	@Column(name = "leave_type", nullable = false)
	private LeaveType leaveType;
	@Column(name = "project_manager_name", nullable = false)
	private String projectManagerName;
	@Column(name = "leave_days")
	private Double days;
	@Column(name = "unpaid_leaves")
	private Double unPaidLeave;
	@Column(name = "team_lead_name", nullable = false)
	private String teamLeadName;

}
