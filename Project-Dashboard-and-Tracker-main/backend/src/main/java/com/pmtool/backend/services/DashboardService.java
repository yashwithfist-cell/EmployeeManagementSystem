package com.pmtool.backend.services;

import java.util.List;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;

public interface DashboardService {
    DashboardStatsDTO getStats();
    List<ChartDataDTO> getHoursByProject();
    List<ChartDataDTO> getHoursByEmployee();
}
