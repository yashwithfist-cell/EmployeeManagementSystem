package com.pmtool.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pmtool.backend.enums.AssignmentStatus;
import com.pmtool.backend.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_assignment")
public class ProjectAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proj_assign_id")
	private long projAssignId;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne
	@JoinColumn(name = "milestone_id", referencedColumnName = "id")
	private Milestone milestone;

	@ManyToOne
	@JoinColumn(name = "discipline_id", referencedColumnName = "id")
	private Discipline discipline;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	private AssignmentStatus status;

	@Column(name = "head_name")
	private String headName;

	@Column(name = "comment")
	private String comment;

	@Column(name = "total_work_sec", nullable = false)
	private Long totalWorkedSeconds = 0L;

	@Column(name = "last_started_at")
	private LocalDateTime lastStartedAt;

	@Column(name = "time_running", nullable = false)
	private boolean timerRunning = false;

	@Column(name = "head_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private TaskStatus headStatus = TaskStatus.PENDING;

	@Column(name = "head_comment")
	private String headComment;

	@Column(name = "finalized")
	private Boolean finalized = false;

}
