package com.pmtool.backend.DTO;

public class ChartDataDTO {
	private String name;
	private Double value;

	public ChartDataDTO(String name, Double value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public Double getValue() {
		return this.value;
	}
}
