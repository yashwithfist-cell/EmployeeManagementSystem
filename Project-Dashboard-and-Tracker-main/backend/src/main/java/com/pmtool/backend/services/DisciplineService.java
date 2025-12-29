package com.pmtool.backend.services;

import com.pmtool.backend.DTO.DisciplineDTO; // Corrected package name if needed
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Helper method to convert an Entity to a DTO
    private DisciplineDTO convertToDTO(Discipline discipline) {
        // Includes the projectId for context, which is good practice
        return new DisciplineDTO(discipline.getId(), discipline.getName(), discipline.getProject().getId());
    }

    // Helper method for the simple list (used by global dropdowns if you ever need them)
    private DisciplineDTO convertToSimpleDTO(Discipline discipline) {
        return new DisciplineDTO(discipline.getId(), discipline.getName());
    }

    /**
     * Retrieves a list of all disciplines.
     * @return A list of DisciplineDTOs.
     */
    @Transactional(readOnly = true)
    public List<DisciplineDTO> getAllDisciplines() {
        return disciplineRepository.findAll().stream()
                .map(this::convertToDTO) // Using the richer DTO is better
                .collect(Collectors.toList());
    }

    /**
     * Creates a new discipline and links it to a project.
     * @param disciplineDTO The DTO containing the name and projectId.
     * @return The created discipline as a DTO.
     */
    @Transactional
    public DisciplineDTO createDiscipline(DisciplineDTO disciplineDTO) {
        Project project = projectRepository.findById(disciplineDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + disciplineDTO.getProjectId()));

        Discipline discipline = new Discipline();
        discipline.setName(disciplineDTO.getName());
        discipline.setProject(project);

        Discipline savedDiscipline = disciplineRepository.save(discipline);
        return convertToDTO(savedDiscipline);
    }

    /**
     * Updates an existing discipline's name and potentially its project.
     * @param id The ID of the discipline to update.
     * @param disciplineDTO The DTO with the new data.
     * @return The updated discipline as a DTO.
     */
    @Transactional
    public DisciplineDTO updateDiscipline(Long id, DisciplineDTO disciplineDTO) {
        Discipline existingDiscipline = disciplineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discipline not found with id: " + id));

        existingDiscipline.setName(disciplineDTO.getName());

        // Handle changing the project if a new projectId is provided
        if (disciplineDTO.getProjectId() != null && !disciplineDTO.getProjectId().equals(existingDiscipline.getProject().getId())) {
            Project newProject = projectRepository.findById(disciplineDTO.getProjectId())
                    .orElseThrow(() -> new RuntimeException("New project not found with id: " + disciplineDTO.getProjectId()));
            existingDiscipline.setProject(newProject);
        }

        Discipline updatedDiscipline = disciplineRepository.save(existingDiscipline);
        return convertToDTO(updatedDiscipline);
    }

    /**
     * Deletes a discipline by its ID.
     * @param id The ID of the discipline to delete.
     */
    @Transactional
    public void deleteDiscipline(Long id) {
        if (!disciplineRepository.existsById(id)) {
            throw new RuntimeException("Discipline not found with id: " + id);
        }
        disciplineRepository.deleteById(id);
    }
}