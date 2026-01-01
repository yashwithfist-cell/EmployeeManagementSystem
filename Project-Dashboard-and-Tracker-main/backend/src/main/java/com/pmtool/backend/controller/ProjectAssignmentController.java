package com.pmtool.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.request.HeadActionRequest;
import com.pmtool.backend.DTO.request.ProjectAssignmentRequest;
import com.pmtool.backend.DTO.response.ApiResponse;
import com.pmtool.backend.DTO.response.ProjectAssignmentResponse;
import com.pmtool.backend.enums.TaskStatus;
import com.pmtool.backend.services.ProjectAssignmentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("api/assignment")
@RequiredArgsConstructor
public class ProjectAssignmentController {

	private final ProjectAssignmentService assignmentService;

	@PostMapping("/assignProject")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER')")
	public ResponseEntity<ApiResponse> assignProject(@RequestBody ProjectAssignmentRequest assignment,
			HttpServletRequest request, Authentication authentication) {
		ProjectAssignmentResponse projectAssignment = assignmentService.assignProject(assignment, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(projectAssignment, "Project assigned successfully", request.getRequestURI()));
	}

	@GetMapping("/getAllAssignments")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER','EMPLOYEE')")
	public ResponseEntity<ApiResponse> getAllAssignments(HttpServletRequest request, Authentication authentication) {
		List<ProjectAssignmentResponse> assignments = assignmentService.getAllAssignments(authentication);
		return ResponseEntity
				.ok(ApiResponse.success(assignments, "Assignments fetched successfully", request.getRequestURI()));
	}

	@PutMapping("/assign/{editId}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER','EMPLOYEE')")
	public ResponseEntity<ApiResponse> updateAssignment(@PathVariable Long editId,
			@RequestBody ProjectAssignmentRequest requestBody, HttpServletRequest request,
			Authentication authentication) {
		ProjectAssignmentResponse assignment = assignmentService.updateAssignment(editId, requestBody, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(assignment, "Assignment updated successfully", request.getRequestURI()));
	}

	@DeleteMapping("/deleteAssignment/{id}")
	public ResponseEntity<Void> deleteAssignment(@PathVariable Long id, HttpServletRequest request) {
		assignmentService.deleteAssignmentById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("assign/{projectId}/milestones")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<MilestoneDTO>> getMilestonesByProjAssignId(@PathVariable Long projectId,
			Authentication authentication) {
		List<MilestoneDTO> milestones = assignmentService.getMilestonesByProjAssignId(projectId, authentication);
		return ResponseEntity.ok(milestones);
	}

	@GetMapping("assign/{projectId}/disciplines")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<DisciplineDTO>> getDisciplinesByProjAssignId(@PathVariable Long projectId,
			Authentication authentication) {
		List<DisciplineDTO> disciplines = assignmentService.getDisciplinesByProjAssignId(projectId, authentication);
		return ResponseEntity.ok(disciplines);
	}

	@PatchMapping("/updateStatus/{id}/{newStatus}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','TEAM_LEAD')")
	public ResponseEntity<ApiResponse> updateAssignmentStatus(@PathVariable Long id, @PathVariable String newStatus,
			HttpServletRequest request, Authentication authentication) {
		ProjectAssignmentResponse assignment = assignmentService.updateAssignmentStatus(id, newStatus, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(assignment, "Assignment status updated successfully", request.getRequestURI()));
	}

	@PatchMapping("/comment/{id}/{text}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','TEAM_LEAD')")
	public ResponseEntity<ApiResponse> updateAssignmentComment(@PathVariable Long id, @PathVariable String text,
			HttpServletRequest request, Authentication authentication) {
		ProjectAssignmentResponse assignment = assignmentService.updateAssignmentComment(id, text, authentication);
		return ResponseEntity.ok(
				ApiResponse.success(assignment, "Assignment comment updated successfully", request.getRequestURI()));
	}

	@GetMapping("/getAllTeamLeadAssignments")
	@PreAuthorize("hasRole('TEAM_LEAD')")
	public ResponseEntity<ApiResponse> getAllTeamLeadAssignments(HttpServletRequest request,
			Authentication authentication) {
		List<ProjectAssignmentResponse> assignments = assignmentService.getAllTeamLeadAssignments(authentication);
		return ResponseEntity
				.ok(ApiResponse.success(assignments, "Assignments fetched successfully", request.getRequestURI()));
	}

	@GetMapping("/comments/{id}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','TEAM_LEAD')")
	public ResponseEntity<ApiResponse> getComments(@PathVariable Long id, HttpServletRequest request,
			Authentication authentication) {
		List<String> comments = assignmentService.getComments(id, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(comments, "comments fetched successfully", request.getRequestURI()));
	}

	@PatchMapping("/startTimer/{id}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER','EMPLOYEE')")
	public ResponseEntity<ApiResponse> startTimer(@PathVariable Long id, HttpServletRequest request) {
		ProjectAssignmentResponse updated = assignmentService.startTimer(id);
		return ResponseEntity.ok(ApiResponse.success(updated, "start time updated", request.getRequestURI()));
	}

	@PatchMapping("/pauseTimer/{id}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER','EMPLOYEE')")
	public ResponseEntity<ApiResponse> pauseTimer(@PathVariable Long id, HttpServletRequest request) {
		ProjectAssignmentResponse updated = assignmentService.pauseTimer(id);
		return ResponseEntity.ok(ApiResponse.success(updated, "pause time updated", request.getRequestURI()));
	}

	@PatchMapping("/stopTimer/{id}")
	@PreAuthorize("hasAnyRole('TEAM_LEAD','PROJECT_MANAGER','EMPLOYEE')")
	public ResponseEntity<ApiResponse> stopTimer(@PathVariable Long id, HttpServletRequest request) {
		ProjectAssignmentResponse updated = assignmentService.stopTimer(id);
		return ResponseEntity.ok(ApiResponse.success(updated, "stop time updated", request.getRequestURI()));
	}

	@PatchMapping("/updateHeadStatus/{id}")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD')")
	public ResponseEntity<ApiResponse> updateHeadStatus(@PathVariable Long id,
			@RequestBody HeadActionRequest requestBody, HttpServletRequest request) {
		ProjectAssignmentResponse assignment = assignmentService.updateHeadStatus(id, requestBody.getTaskStatus(),
				requestBody.getHeadComment());
		return ResponseEntity
				.ok(ApiResponse.success(assignment, "Assignment status updated successfully", request.getRequestURI()));
	}

}
