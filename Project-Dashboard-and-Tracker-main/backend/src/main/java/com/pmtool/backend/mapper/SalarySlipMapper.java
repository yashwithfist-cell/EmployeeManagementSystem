package com.pmtool.backend.mapper;

import com.pmtool.backend.DTO.SalarySlipDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SalarySlip;

public interface SalarySlipMapper {
	public SalarySlipDto convertToSalarySlipDto(SalarySlip salarySlip);
	public SalarySlip convertToSalarySlip(SalarySlipDto salarySlipDto,Employee employee);
}
