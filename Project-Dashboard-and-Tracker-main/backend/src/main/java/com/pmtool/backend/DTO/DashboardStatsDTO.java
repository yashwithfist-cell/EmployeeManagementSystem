package com.pmtool.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDTO {
	private long totalProjects;
	private long totalEmployees;
	private long totalDepartments;
	private long totalMilestones;
}
