package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
	public List<Leave> findByProjectManagerName(String projectManagerName);

	@Query("""
			    SELECT SUM(l.unPaidLeave)
			    FROM Leave l
			    WHERE l.employee.username = :username
			      AND l.status = 'APPROVED_BY_SYSTEM_ADMIN'
			      AND l.startDate BETWEEN :startDate AND :endDate
			      AND l.endDate BETWEEN :startDate AND :endDate
			""")
	public Optional<Double> sumUnpaidLeaveByUsername(@Param("username") String username,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("""
			    SELECT SUM(l.days)
			    FROM Leave l
			    WHERE l.employee.username = :username
			      AND l.leaveType = 'EARNED'
			      AND l.status = 'APPROVED_BY_SYSTEM_ADMIN'
			      AND l.startDate BETWEEN :startOfYear AND :endOfYear
			      AND l.endDate BETWEEN :startOfYear AND :endOfYear
			""")
	public Optional<Double> sumPaidLeaveByUsername(@Param("username") String username,
			@Param("startOfYear") LocalDate startOfYear, @Param("endOfYear") LocalDate endOfYear);

	@Query("""
			    SELECT SUM(l.days)
			    FROM Leave l
			    WHERE l.employee.username = :username
			      AND l.leaveType = 'CASUAL'
			      AND l.status = 'APPROVED_BY_SYSTEM_ADMIN'
			      AND l.startDate BETWEEN :startOfYear AND :endOfYear
			      AND l.endDate BETWEEN :startOfYear AND :endOfYear
			""")
	public Optional<Double> sumCasualLeaveByUsername(@Param("username") String username,
			@Param("startOfYear") LocalDate startOfYear, @Param("endOfYear") LocalDate endOfYear);

	@Query("SELECT l FROM Leave l WHERE l.employee.username=:username")
	public List<Leave> findByEmployeeName(@Param("username") String username);

	public List<Leave> findByTeamLeadName(String teamLeadName);

//	@Query("""
//		    SELECT l
//		    FROM Leave l
//		    WHERE l.employee.username = :username
//		      AND (l.startDate BETWEEN :startDate AND :endDate)
//			      OR (l.endDate BETWEEN :startDate AND :endDate)
//		""")
	@Query("""
		    SELECT l
		    FROM Leave l
		    WHERE l.employee.username = :username
		      AND l.startDate <= :endDate
		      AND l.endDate >= :startDate
		""")

	public List<Leave> findAllLeaveDates(@Param("username") String username, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
	@Query("""
		    SELECT COALESCE(SUM(l.days), 0.0)
		    FROM Leave l
		    WHERE l.employee.username = :username AND status='APPROVED_BY_SYSTEM_ADMIN' AND l.leaveType = 'CASUAL'
		      AND l.startDate <= :endDate
		      AND l.endDate >= :startDate
		""")
	public Double findCurrentMonthLeaves(@Param("username") String username, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

}
