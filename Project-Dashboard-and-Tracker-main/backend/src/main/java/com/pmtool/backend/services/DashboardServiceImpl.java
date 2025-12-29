package com.pmtool.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;
import com.pmtool.backend.DTO.HoursDTO;
import com.pmtool.backend.repository.DepartmentRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectRepository;
import com.pmtool.backend.repository.WorkLogEntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{
	private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final MilestoneRepository milestoneRepository;
    private final WorkLogEntryRepository workLogEntryRepository;

    @Override
    public DashboardStatsDTO getStats() {
        return new DashboardStatsDTO(
                projectRepository.count(),
                employeeRepository.count(),
                departmentRepository.count(),
                milestoneRepository.count()
        );
    }

    @Override
    public List<ChartDataDTO> getHoursByProject() {
        return workLogEntryRepository.findHoursPerProject();
    }

    @Override
    public List<ChartDataDTO> getHoursByEmployee() {
        return workLogEntryRepository.findHoursPerEmployee();
    }
}
