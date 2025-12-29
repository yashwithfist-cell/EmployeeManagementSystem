package com.pmtool.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "worklog_entries")
public class WorkLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id") // Can be null for tasks like "Break"
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id") // Can be null
    private Discipline discipline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id") // Can be null
    private Milestone milestone;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(name = "task", nullable = false)
    private String task;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;

    // Inside your WorkLogEntry.java class

    @PrePersist // Runs before a new entity is saved
    @PreUpdate  // Runs before an existing entity is updated
    public void calculateHoursWorked() {
        if (startTime != null && endTime != null) {
            // Calculate duration and set it in hours (as a double)
            java.time.Duration duration = java.time.Duration.between(startTime, endTime);
            this.hoursWorked = duration.toMillis() / (60.0 * 60.0 * 1000.0);
        } else {
            this.hoursWorked = 0.0;
        }
    }

    public WorkLogEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Double getHoursWorked() {
        return hoursWorked;

    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }


}