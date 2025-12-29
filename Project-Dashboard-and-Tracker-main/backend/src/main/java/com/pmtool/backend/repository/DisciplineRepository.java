package com.pmtool.backend.repository;

import com.pmtool.backend.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    // THIS IS THE METHOD THAT THE SERVICE NEEDS TO FIND.
    // Ensure the name and parameters are exactly as written.
    @Query("SELECT d FROM Discipline d WHERE d.project.id = :projectId")
    List<Discipline> findDisciplinesByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT d FROM Discipline d WHERE d.id IN :ids")
    List<Discipline> findByIds(@Param("ids") List<Long> ids);
}