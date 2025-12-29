package com.pmtool.backend.DTO;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

	@NotBlank(message = "EmployeeId is required")
	private String employeeId;

	@Valid
	@NotNull(message = "Department is required")
	private DepartmentDTO department;

	@NotBlank(message = "EmployeeName is required")
	private String employeeName;

	@NotBlank(message = "Role is required")
	private String role;

	@NotBlank(message = "Join date is required")
	private LocalDate joinDate;

	@NotBlank(message = "Prof Period end date is required")
	private LocalDate profPeriodEndDate;
	@NotBlank(message = "location is required")
	private String location;
	@NotBlank(message = "Bank Name is required")
	private String bankName;
	@NotBlank(message = "Bank Account Number is required")
	private String bankAccountNo;
	@NotBlank(message = "Salary is required")
	private String salary;
}
