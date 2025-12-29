package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.AttendanceLog;
import com.pmtool.backend.entity.Employee;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {

//	@Query(value = "SELECT e.* " + "FROM employees e " + "LEFT JOIN attendance_log a ON e.employee_id = a.employee_id "
//			+ "WHERE a.employee_id IS NULL", nativeQuery = true)
//	List<Employee> findAllAbsentEmployees();

	@Query("SELECT e FROM Employee e WHERE e.employeeId NOT IN (SELECT al.employee.employeeId FROM AttendanceLog al)")
	List<Employee> findAllAbsentEmployees();

	@Query("SELECT a FROM AttendanceLog a " + "WHERE a.date BETWEEN :fromDate AND :toDate " + "ORDER BY a.date ASC")
	List<AttendanceLog> getAttendanceBetweenDates(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	@Query("SELECT a FROM AttendanceLog a " + "WHERE a.date BETWEEN :fromDate AND :toDate "
			+ "AND a.employee.username =:username " + "ORDER BY a.date ASC")
	List<AttendanceLog> getAttendanceByEmpBetweenDates(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate, @Param("username") String username);

}
