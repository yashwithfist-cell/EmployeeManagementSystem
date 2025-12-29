package com.pmtool.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pmtool.backend.entity.Training;

public interface TrainingRepository extends JpaRepository<Training, Long>{

}
