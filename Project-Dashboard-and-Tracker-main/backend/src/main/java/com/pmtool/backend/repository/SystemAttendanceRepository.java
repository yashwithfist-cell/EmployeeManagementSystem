package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.SystemAttendance;

public interface SystemAttendanceRepository extends JpaRepository<SystemAttendance, Long> {
	@Query("SELECT s FROM SystemAttendance s " + "WHERE s.employee.username = :username "
			+ "AND s.date BETWEEN :fromDate AND :toDate")
	List<SystemAttendance> findAllByDate(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
			@Param("username") String username);
	
	@Query("SELECT s FROM SystemAttendance s " + "WHERE s.date BETWEEN :fromDate AND :toDate")
	List<SystemAttendance> findAllByDate(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	SystemAttendance findByDate(LocalDate date);

//	SystemAttendance findTopByEmployeeOrderByIdDesc(Employee employee);

	@Query("SELECT s FROM SystemAttendance s WHERE s.employee.username = :username AND s.date = :date")
	SystemAttendance findByDateAndEmployee(@Param("username") String username, @Param("date") LocalDate date);

	@Query("SELECT s FROM SystemAttendance s WHERE s.employee.username = :username")
	List<SystemAttendance> findAllByUser(@Param("username") String username);
}
