package com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "academic_levels")
public class AcademicLevelEntity {

    @Id
    private String id;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getSchoolId() { return schoolId; }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public void setName(String name) { this.name = name; }
    public void setNumber(int number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
