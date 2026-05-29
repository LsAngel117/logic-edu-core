package com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "academic_structures")
public class AcademicStructureEntity {

    @Id
    private String id;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(name = "structure_type", nullable = false)
    private String structureType;

    @Column(name = "levels_count", nullable = false)
    private int levelsCount;

    @Column(name = "periods_per_level", nullable = false)
    private int periodsPerLevel;

    @Column(name = "evaluation_periods_per_period")
    private int evaluationPeriodsPerPeriod;

    @Column(name = "subjects_per_period", nullable = false)
    private int subjectsPerPeriod;

    @Column(name = "hours_per_subject", nullable = false)
    private int hoursPerSubject;

    private boolean active;

    private int version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getSchoolId() { return schoolId; }
    public String getStructureType() { return structureType; }
    public int getLevelsCount() { return levelsCount; }
    public int getPeriodsPerLevel() { return periodsPerLevel; }
    public int getEvaluationPeriodsPerPeriod() { return evaluationPeriodsPerPeriod; }
    public int getSubjectsPerPeriod() { return subjectsPerPeriod; }
    public int getHoursPerSubject() { return hoursPerSubject; }
    public boolean isActive() { return active; }
    public int getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public void setStructureType(String structureType) { this.structureType = structureType; }
    public void setLevelsCount(int levelsCount) { this.levelsCount = levelsCount; }
    public void setPeriodsPerLevel(int periodsPerLevel) { this.periodsPerLevel = periodsPerLevel; }
    public void setEvaluationPeriodsPerPeriod(int evaluationPeriodsPerPeriod) { this.evaluationPeriodsPerPeriod = evaluationPeriodsPerPeriod; }
    public void setSubjectsPerPeriod(int subjectsPerPeriod) { this.subjectsPerPeriod = subjectsPerPeriod; }
    public void setHoursPerSubject(int hoursPerSubject) { this.hoursPerSubject = hoursPerSubject; }
    public void setActive(boolean active) { this.active = active; }
    public void setVersion(int version) { this.version = version; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
