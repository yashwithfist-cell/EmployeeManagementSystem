package com.pmtool.backend.DTO;

import java.time.LocalDate;
import com.pmtool.backend.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAssignmentDto {

	private long projAssignId;
	private EmployeeDTO employeeDTO;
	private ProjectDTO projectDTO;
	private MilestoneDTO milestoneDTO;
	private DisciplineDTO disciplineDTO;
	private LocalDate startDate;
	private LocalDate dueDate;
	private AssignmentStatus status;
}
