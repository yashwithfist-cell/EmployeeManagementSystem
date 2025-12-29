package com.pmtool.backend.mapper;

import org.springframework.stereotype.Component;

import com.pmtool.backend.DTO.SalarySlipDto;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SalarySlip;

@Component
public class SalarySlipMapperImpl implements SalarySlipMapper {

	@Override
	public SalarySlipDto convertToSalarySlipDto(SalarySlip salarySlip) {
		return SalarySlipDto.builder().id(salarySlip.getId()).bankAccountNo(salarySlip.getEmployee().getBankAccountNo())
				.bankName(salarySlip.getEmployee().getBankName())
				.department(salarySlip.getEmployee().getDepartment().getName())
				.employeeId(salarySlip.getEmployee().getEmployeeId()).employeeName(salarySlip.getEmployee().getName())
				.location(salarySlip.getEmployee().getLocation()).month(salarySlip.getMonth())
				.salary(salarySlip.getEmployee().getSalary()).build();
	}

	@Override
	public SalarySlip convertToSalarySlip(SalarySlipDto salarySlipDto, Employee employee) {
		return SalarySlip.builder().id(salarySlipDto.getId()).employee(employee).month(salarySlipDto.getMonth())
				.build();
	}

}
