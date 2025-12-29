package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.services.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
@CrossOrigin
public class DisciplineController {

    @Autowired
    private DisciplineService disciplineService;

    /**
     * GET /api/disciplines/all
     * Provides a simple list of all disciplines for admin dropdowns.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DisciplineDTO>> getAllDisciplinesForDropdown() {
        List<DisciplineDTO> disciplines = disciplineService.getAllDisciplines();
        return ResponseEntity.ok(disciplines);
    }

    /**
     * POST /api/disciplines
     * Creates a new discipline. Restricted to SYSTEM_ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<DisciplineDTO> createDiscipline(@RequestBody DisciplineDTO disciplineDTO) {
        DisciplineDTO createdDiscipline = disciplineService.createDiscipline(disciplineDTO);
        return new ResponseEntity<>(createdDiscipline, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/disciplines/{id}
     * Deletes a discipline by its ID. Restricted to SYSTEM_ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteDiscipline(@PathVariable Long id) {
        disciplineService.deleteDiscipline(id);
        return ResponseEntity.noContent().build();
    }
}