package com.pmtool.backend.services;

import com.pmtool.backend.DTO.DetailedReportDTO;
import com.pmtool.backend.DTO.ReportDTO;
import com.pmtool.backend.repository.WorkLogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service

public class ReportService {
    @Autowired
    private WorkLogEntryRepository workLogEntryRepository;

    public List<ReportDTO> generateReport(LocalDate startDate, LocalDate endDate) {
        return workLogEntryRepository.getProjectReportData(startDate,endDate);
    }
    public List<ReportDTO> generateMilestoneReport(Long milestoneId){
        return workLogEntryRepository.getMilestoneReportData(milestoneId);
    }
    // In ReportService.java

    public List<DetailedReportDTO> generateDetailedReport(Long projectId, Long milestoneId) {
        return workLogEntryRepository.getDetailedReportData(projectId, milestoneId);
    }
}


