package com.pmtool.backend.services;

import com.pmtool.backend.DTO.WorkLogEntryDTO; // Corrected package name if needed
import com.pmtool.backend.DTO.WorkLogResponseDTO; // Corrected package name if needed
import com.pmtool.backend.entity.*;
import com.pmtool.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkLogService {

    @Autowired private WorkLogEntryRepository workLogEntryRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private DisciplineRepository disciplineRepository;

    /**
     * Saves a new worklog entry and returns it as a DTO.
     * This prevents JSON serialization errors caused by circular references in entities.
     * @param dto The WorkLogEntryDTO from the frontend request.
     * @param username The username of the authenticated employee.
     * @return A WorkLogResponseDTO representing the saved entry.
     */
    @Transactional
    public WorkLogResponseDTO saveNewEntry(WorkLogEntryDTO dto, String username) { // <-- FIX #1: Return type changed to DTO
        // 1. Find the logged-in employee entity
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated employee not found in database"));

        // 2. Create a new WorkLogEntry entity
        WorkLogEntry newEntry = new WorkLogEntry();
        newEntry.setEmployee(employee);
        newEntry.setDate(dto.getDate());
        newEntry.setTask(dto.getTask());
        newEntry.setDescription(dto.getDescription());
        newEntry.setStartTime(dto.getStartTime());
        newEntry.setEndTime(dto.getEndTime());

        // 3. Find and attach related entities
        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
            newEntry.setProject(project);
        }
        if (dto.getMilestoneId() != null) {
            Milestone milestone = milestoneRepository.findById(dto.getMilestoneId())
                    .orElseThrow(() -> new RuntimeException("Milestone not found with id: " + dto.getMilestoneId()));
            newEntry.setMilestone(milestone);
        }
        if (dto.getDisciplineId() != null) {
            Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
                    .orElseThrow(() -> new RuntimeException("Discipline not found with id: " + dto.getDisciplineId()));
            newEntry.setDiscipline(discipline);
        }

        // 4. Save the entity to the database
        WorkLogEntry savedEntity = workLogEntryRepository.save(newEntry);

        // 5. --- THIS IS THE FIX ---
        // Convert the saved entity into a "safe" DTO before returning it.
        // The DTO does not have circular references and is designed for JSON conversion.
        return new WorkLogResponseDTO(savedEntity);
    }

    /**
     * Retrieves all worklog entries for a specific employee for the current calendar month.
     * @param username The username of the employee.
     * @return A list of DTOs ready for the UI.
     */
    @Transactional(readOnly = true)
    public List<WorkLogResponseDTO> getEntriesForCurrentMonth(String username) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        List<WorkLogEntry> entries = workLogEntryRepository.findByEmployee_UsernameAndDateBetweenOrderByDateAsc(
                username,
                startOfMonth,
                endOfMonth
        );

        // Convert the list of entities to a list of DTOs
        return entries.stream()
                .map(WorkLogResponseDTO::new)
                .collect(Collectors.toList());
    }
}