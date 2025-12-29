package com.pmtool.backend.services;

import com.pmtool.backend.DTO.SalarySlipDto;

public interface SalarySlipService {
	public SalarySlipDto calculateSalary(String username, String date);
}
