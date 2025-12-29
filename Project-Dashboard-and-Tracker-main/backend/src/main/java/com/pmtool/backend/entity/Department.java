package com.pmtool.backend.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "department_name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Employee> employees;


    public Department() {}
    public Department(String name) { this.name = name; }

    // --- CORRECTED GETTERS AND SETTERS ---
    public Long getId() { // Corrected: follows convention for field 'id'
        return id;
    }

    public void setId(Long id) { // Corrected
        this.id = id;
    }

    public String getName() { // Corrected: follows convention for field 'name'
        return name;
    }

    public void setName(String name) { // Corrected
        this.name = name;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}

