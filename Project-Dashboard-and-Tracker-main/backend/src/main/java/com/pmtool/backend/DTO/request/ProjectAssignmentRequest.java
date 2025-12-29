package com.pmtool.backend.DTO.request;

import java.time.LocalDate;
import java.util.Set;

import com.pmtool.backend.enums.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectAssignmentRequest {
	@NotNull
	private String teamLeadId;
	@NotNull
	private Long projectId;
	@NotNull
	private Long milestoneId;
	@NotNull
	private Long disciplineId;
	@NotNull
	private LocalDate startDate;
	@NotNull
	private LocalDate dueDate;
	@NotNull
	private AssignmentStatus status;
}
