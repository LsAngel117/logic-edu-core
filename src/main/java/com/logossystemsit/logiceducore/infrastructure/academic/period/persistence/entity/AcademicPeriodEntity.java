package com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "academic_periods")
public class AcademicPeriodEntity {

    @Id
    private String id;

    @Column(name = "level_id", nullable = false)
    private String levelId;

    @Column(name = "period_type", nullable = false)
    private String periodType;

    @Column(nullable = false)
    private String name;

    private int sequence;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getLevelId() { return levelId; }
    public String getPeriodType() { return periodType; }
    public String getName() { return name; }
    public int getSequence() { return sequence; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setLevelId(String levelId) { this.levelId = levelId; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public void setName(String name) { this.name = name; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
