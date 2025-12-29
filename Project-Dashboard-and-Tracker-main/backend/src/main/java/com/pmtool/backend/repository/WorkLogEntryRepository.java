package com.pmtool.backend.repository;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DetailedReportDTO;
import com.pmtool.backend.DTO.ReportDTO;
import com.pmtool.backend.entity.WorkLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository // <-- BEST PRACTICE: Add the @Repository annotation
public interface WorkLogEntryRepository extends JpaRepository<WorkLogEntry, Long> {

    // For the employee's own timesheet view
    List<WorkLogEntry> findByEmployee_UsernameAndDateBetweenOrderByDateAsc(
            String username,
            LocalDate startDate,
            LocalDate endDate
    );

    // For the admin's Master Data (Project-based) report
    @Query("SELECT new com.pmtool.backend.DTO.ReportDTO(e.name, p.name, SUM(w.hoursWorked)) " +
            "FROM WorkLogEntry w " +
            "JOIN w.employee e " +
            "JOIN w.project p " +
            "WHERE w.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.name, p.name " +
            "ORDER BY e.name, p.name")
    List<ReportDTO> getProjectReportData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // For the admin's Milestone Data report
    @Query("SELECT new com.pmtool.backend.DTO.ReportDTO(e.name, d.name, SUM(w.hoursWorked)) " +
            "FROM WorkLogEntry w JOIN w.employee e JOIN w.discipline d " +
            "WHERE w.milestone.id = :milestoneId " +
            "GROUP BY e.name, d.name")
    List<ReportDTO> getMilestoneReportData(@Param("milestoneId") Long milestoneId);

    // For the Dashboard Bar Chart: Hours per Project
    @Query("SELECT new com.pmtool.backend.DTO.ChartDataDTO(p.name, SUM(w.hoursWorked)) " +
            "FROM WorkLogEntry w JOIN w.project p " +
            "GROUP BY p.name ORDER BY SUM(w.hoursWorked) DESC")
    List<ChartDataDTO> findHoursPerProject();

    // For the Dashboard Pie Chart: Hours per Employee
    @Query("SELECT new com.pmtool.backend.DTO.ChartDataDTO(e.name, SUM(w.hoursWorked)) " +
            "FROM WorkLogEntry w JOIN w.employee e " +
            "GROUP BY e.name")
    List<ChartDataDTO> findHoursPerEmployee();

    // For a more advanced, detailed report (if you need it later)
    @Query("SELECT new com.pmtool.backend.DTO.DetailedReportDTO(e.name, d.name, SUM(w.hoursWorked)) " +
            "FROM WorkLogEntry w JOIN w.employee e JOIN w.discipline d " +
            "WHERE w.project.id = :projectId " +
            "AND (:milestoneId IS NULL OR w.milestone.id = :milestoneId) " +
            "GROUP BY e.name, d.name")
    List<DetailedReportDTO> getDetailedReportData(
            @Param("projectId") Long projectId,
            @Param("milestoneId") Long milestoneId
    );

} // <-- The interface definition ends here. There should be nothing after this brace.