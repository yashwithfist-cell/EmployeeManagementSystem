package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.DepartmentDTO; // You will need to create this DTO
import com.pmtool.backend.repository.DepartmentRepository;
import com.pmtool.backend.services.DepartmentService; // You will need to create this Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService; // Use a service layer

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(departmentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, departmentDTO));
    }
    @GetMapping("/search")
    public ResponseEntity<?> getDepartmentByName(@RequestParam String name) {
        // This call is now updated to use the corrected repository method name
        return departmentRepository.findByName(name) // <-- FIXED
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}