package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pmtool.backend.entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

	@Query("SELECT h.holidayDate FROM Holiday h ORDER BY h.holidayDate ASC")
	List<LocalDate> findAllHolidayDates();

}
