package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.MilestoneDTO; // You already have this DTO
import com.pmtool.backend.DTO.MilestoneResponseDTO;
import com.pmtool.backend.services.MilestoneService; // Create this service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@CrossOrigin
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @GetMapping
    public ResponseEntity<List<MilestoneResponseDTO>> getAllMilestones() {
        return ResponseEntity.ok(milestoneService.getAllMilestones());
    }


    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<MilestoneResponseDTO> createMilestone(@RequestBody MilestoneDTO milestoneDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(milestoneService.createMilestone(milestoneDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) { // <-- FIXED
        milestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<MilestoneResponseDTO> updateMilestone(@PathVariable Long id, @RequestBody MilestoneDTO milestoneDTO) { // <-- FIXED
        return ResponseEntity.ok(milestoneService.updateMilestone(id, milestoneDTO));
    }
    
    @PostMapping("/getUserMilestones")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SYSTEM_ADMIN','TEAM_LEAD')")
    public ResponseEntity<List<MilestoneResponseDTO>> getUserMilestones(@RequestBody  List<Long> projectIds) {
        return ResponseEntity.ok(milestoneService.getUserMilestones(projectIds));
    }
}