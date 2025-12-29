package com.pmtool.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalarySlip {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "salary_month", nullable = false)
	private String month;
	@Column(name = "basic_salary")
	private double basic;
	@Column(name = "da")
	private double da;
	@Column(name = "hra")
	private double hra;
	@Column(name = "conveyance")
	private double conveyance;
	@Column(name = "medical")
	private double medical;
	@Column(name = "special")
	private double special;
	@Column(name = "prof_tax")
	private double professionalTax;
	private double tds;
	@Column(name = "provident_fund")
	private double providentFund;
	@Column(name = "total_days")
	private int totalDays;
	@Column(name = "net_salary")
	private double netSalary;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
	private Employee employee;
}