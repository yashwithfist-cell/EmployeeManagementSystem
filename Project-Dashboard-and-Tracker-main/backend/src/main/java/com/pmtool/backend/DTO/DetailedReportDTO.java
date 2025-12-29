package com.pmtool.backend.DTO;

public class DetailedReportDTO {

	private String employeeName;
	private String disciplineName;
	private Double totalHours;

	public DetailedReportDTO(String employeeName, String disciplineName, Double totalHours) {
		this.employeeName = employeeName;
		this.disciplineName = disciplineName;
		this.totalHours = totalHours;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public String getDisciplineName() {
		return this.disciplineName;
	}

	public Double getTotalHours() {
		return this.totalHours;
	}


}
