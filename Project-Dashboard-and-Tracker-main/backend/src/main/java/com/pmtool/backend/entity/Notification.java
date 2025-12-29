package com.pmtool.backend.entity;

import java.time.LocalDateTime;

import com.pmtool.backend.enums.Status;
import com.pmtool.backend.enums.TrainingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
	private Employee employee;

	@Column(name = "user_name")
	private String username;

	@Column(name = "title")
	private String title;

	@Column(name = "message")
	private String message;

	@Column(name = "read_status")
	private boolean readStatus = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "is_prob_Notif")
	private boolean isProbNotif = false;

	@Column(name = "comment")
	private String comment;

	@Column(name = "pr_status")
	private String prStatus;

	@Column(name = "log_comment")
	private String logComment;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "training_status", nullable = false)
	private TrainingStatus trainingStatus = TrainingStatus.NOT_APPLIED;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status=Status.PENDING;
}
