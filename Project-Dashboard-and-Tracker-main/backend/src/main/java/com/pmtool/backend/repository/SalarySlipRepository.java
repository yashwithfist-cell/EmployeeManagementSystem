package com.pmtool.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pmtool.backend.entity.SalarySlip;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, Long> {
}