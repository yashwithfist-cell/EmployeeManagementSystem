package com.pmtool.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.ProjectAssignment;

public interface ProjectAssignmentRepo extends JpaRepository<ProjectAssignment, Long> {

	@Query("SELECT p FROM ProjectAssignment p WHERE p.headName = :name")
	List<ProjectAssignment> findAllByHeadName(String name);

	@Query("""
			    SELECT DISTINCT m
			    FROM ProjectAssignment pa
			    JOIN pa.milestone m
			    WHERE pa.project.id = :projectId
			      AND pa.employee.username = :username
			""")
	List<Milestone> findMilestonesByProjAssignId(@Param("projectId") Long projectId,
			@Param("username") String username);

	@Query("""
			    SELECT DISTINCT m
			    FROM ProjectAssignment pa
			    JOIN pa.discipline m
			    WHERE pa.project.id = :projectId
			      AND pa.employee.username = :username
			""")
	List<Discipline> findDisciplinesByProjAssignId(@Param("projectId") Long projectId,
			@Param("username") String username);

	@Query("SELECT p FROM ProjectAssignment p WHERE p.employee.username = :username")
	Optional<List<ProjectAssignment>> findAllByUsername(@Param("username") String username);

	@Query("SELECT p.comment from ProjectAssignment p WHERE p.projAssignId = :id")
	Optional<List<String>> findAllCommentById(Long id);

}
