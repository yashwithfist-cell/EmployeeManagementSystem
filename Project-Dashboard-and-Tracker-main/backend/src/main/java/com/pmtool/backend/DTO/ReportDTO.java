package com.pmtool.backend.DTO;

public class ReportDTO {
	private String rowHeader;
	private String columnHeader;
	private Double totalHours;

	public void setRowHeader(String rowHeader) {
		this.rowHeader = rowHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}

	public ReportDTO() {
	}

	public ReportDTO(String rowHeader, String columnHeader, Double totalHours) {
		this.rowHeader = rowHeader;
		this.columnHeader = columnHeader;
		this.totalHours = totalHours;
	}

	public String getRowHeader() {
		return this.rowHeader;
	}

	public String getColumnHeader() {
		return this.columnHeader;
	}

	public Double getTotalHours() {
		return this.totalHours;
	}
}
