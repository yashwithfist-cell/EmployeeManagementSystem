package com.pmtool.backend.DTO;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkLogEntryDTO {
    private LocalDate date;
    private String task;
    private String description;
    private Long projectId;
    private Long milestoneId;
    private Long disciplineId;
    private LocalTime startTime;
    private LocalTime endTime;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }
    public Long getDisciplineId() { return disciplineId; }
    public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}