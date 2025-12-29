package com.pmtool.backend.DTO.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.ProjectAssignment;
import com.pmtool.backend.enums.AssignmentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectAssignmentResponse {
	private Long id;
	private String employeeName;
	private String projectName;
	private String milestoneName;
	private String disciplineName;
	private LocalDate startDate;
	private LocalDate dueDate;
	private AssignmentStatus status;
	private String comment;
	private Long totalWorkedSeconds;
	private LocalDateTime lastStartedAt;
	private boolean timerRunning;
	private String department;

	public static ProjectAssignmentResponse fromEntity(ProjectAssignment pa) {
		return ProjectAssignmentResponse.builder().id(pa.getProjAssignId())
				.employeeName(pa.getEmployee() != null ? pa.getEmployee().getName() : null)
				.projectName(pa.getProject() != null ? pa.getProject().getName() : null).startDate(pa.getStartDate())
				.dueDate(pa.getDueDate()).status(pa.getStatus())
				.milestoneName(pa.getMilestone().getName())
				.disciplineName(pa.getDiscipline().getName())
				.comment(pa.getComment()).totalWorkedSeconds(pa.getTotalWorkedSeconds())
				.timerRunning(pa.isTimerRunning()).department(pa.getEmployee().getDepartment().getName()).build();
	}
}
