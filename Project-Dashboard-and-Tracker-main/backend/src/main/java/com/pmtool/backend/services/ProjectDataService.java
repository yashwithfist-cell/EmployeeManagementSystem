package com.pmtool.backend.services;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.ProjectDTO;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectDataService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private MilestoneRepository milestoneRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;

	@Transactional(readOnly = true)
	public List<ProjectDTO> getAllProjects() {
		return projectRepository.findAll().stream().map(project -> new ProjectDTO(project.getId(), project.getName()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<MilestoneDTO> getMilestonesByProjectId(Long projectId) {
		// This now calls the correct method from MilestoneRepository
		return milestoneRepository.findMilestonesByProjectId(projectId).stream().map(
				milestone -> new MilestoneDTO(milestone.getId(), milestone.getName(), milestone.getProject().getId()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<DisciplineDTO> getDisciplinesByProjectId(Long projectId) {
		// This now calls the correct method from DisciplineRepository, fixing your
		// error
		return disciplineRepository.findDisciplinesByProjectId(projectId).stream()
				.map(discipline -> new DisciplineDTO(discipline.getId(), discipline.getName(),
						discipline.getProject().getId()))
				.collect(Collectors.toList());
	}

}