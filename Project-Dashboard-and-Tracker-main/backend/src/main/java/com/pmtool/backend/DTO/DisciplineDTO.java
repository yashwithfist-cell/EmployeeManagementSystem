package com.pmtool.backend.DTO;

public class DisciplineDTO {
    private Long id;
    private String name;
    private Long projectId;

    public DisciplineDTO() {
    }

    public DisciplineDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public DisciplineDTO(Long id, String name, Long projectId) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}