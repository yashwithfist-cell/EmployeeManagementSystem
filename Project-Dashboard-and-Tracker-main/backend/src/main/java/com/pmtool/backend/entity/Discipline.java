package com.pmtool.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "disciplines")
public class Discipline {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "discipline_name", nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<WorkLogEntry> workLogEntries;

	@OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectAssignment> projectAssignments;

}