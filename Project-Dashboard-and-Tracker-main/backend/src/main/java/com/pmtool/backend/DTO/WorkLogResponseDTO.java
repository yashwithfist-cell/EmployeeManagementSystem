package com.pmtool.backend.DTO;

import com.pmtool.backend.entity.WorkLogEntry;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkLogResponseDTO {
    private Long id;
    private LocalDate date;
    private String task;
    private String description;
    private String projectName;
    private String milestoneName;
    private String disciplineName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double hoursWorked;

    public WorkLogResponseDTO(WorkLogEntry entity) {
        this.id = entity.getId();
        this.date = entity.getDate();
        this.task = entity.getTask();
        this.description = entity.getDescription();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
        this.hoursWorked = entity.getHoursWorked();
        this.projectName = (entity.getProject() != null) ? entity.getProject().getName() : "N/A";
        this.milestoneName = (entity.getMilestone() != null) ? entity.getMilestone().getName() : "N/A";
        this.disciplineName = (entity.getDiscipline() != null) ? entity.getDiscipline().getName() : "N/A";
    }

    // Getters and setters for all fields...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getMilestoneName() { return milestoneName; }
    public void setMilestoneName(String milestoneName) { this.milestoneName = milestoneName; }
    public String getDisciplineName() { return disciplineName; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }
}