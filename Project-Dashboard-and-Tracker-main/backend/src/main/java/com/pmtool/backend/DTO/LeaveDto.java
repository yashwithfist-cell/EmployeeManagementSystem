package com.pmtool.backend.DTO;

import java.time.LocalDate;

import com.pmtool.backend.enums.LeaveStatus;
import com.pmtool.backend.enums.LeaveType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveDto {
	private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String employeeId;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    private String username;
    private String projectManagerName;
    
    private double yearlyCasualLeave;
    private double pendingCasualLeave;
    private double yearlyPaidLeave;
    private double pendingPaidLeave;
    private Double days;
    private Double unPaidLeave;
    private String teamLeadName;
}
