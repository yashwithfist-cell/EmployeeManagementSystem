package com.pmtool.backend.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.request.ProjectAssignmentRequest;
import com.pmtool.backend.DTO.response.ProjectAssignmentResponse;
import com.pmtool.backend.entity.ProjectAssignment;
import com.pmtool.backend.enums.AssignmentStatus;
import com.pmtool.backend.enums.TaskStatus;

public interface ProjectAssignmentService {

	public ProjectAssignmentResponse assignProject(ProjectAssignmentRequest req, Authentication authentication);

	public List<ProjectAssignmentResponse> getAllAssignments(Authentication authentication);

	public ProjectAssignmentResponse updateAssignment(Long id, ProjectAssignmentRequest request,
			Authentication authentication);

	public void deleteAssignmentById(Long id);

	public List<MilestoneDTO> getMilestonesByProjAssignId(Long projectId, Authentication authentication);

	public List<DisciplineDTO> getDisciplinesByProjAssignId(Long projectId, Authentication authentication);

	public ProjectAssignmentResponse updateAssignmentStatus(Long id, String status, Authentication authentication);

	public ProjectAssignmentResponse updateAssignmentComment(Long id, String comment, Authentication authentication);

	public List<ProjectAssignmentResponse> getAllTeamLeadAssignments(Authentication authentication);

	public List<String> getComments(Long id, Authentication authentication);

	public ProjectAssignmentResponse startTimer(Long id);

	public ProjectAssignmentResponse pauseTimer(Long id);

	public ProjectAssignmentResponse stopTimer(Long id);

	public ProjectAssignmentResponse updateHeadStatus(Long id, TaskStatus status, String comment);
}
