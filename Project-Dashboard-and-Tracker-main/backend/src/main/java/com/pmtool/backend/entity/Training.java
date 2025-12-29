package com.pmtool.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "training")
public class Training {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tr_id")
	private Long trId;
	@Column(name = "start_time")
	private LocalDateTime startTime;
	@Column(name = "end_time")
	private LocalDateTime endTime;
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	private Employee employee;
}
