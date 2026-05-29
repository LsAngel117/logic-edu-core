package com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "evaluation_periods")
public class EvaluationPeriodEntity {

    @Id
    private String id;

    @Column(name = "period_id", nullable = false)
    private String periodId;

    @Column(nullable = false)
    private String name;

    private int sequence;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getPeriodId() { return periodId; }
    public String getName() { return name; }
    public int getSequence() { return sequence; }
    public BigDecimal getWeight() { return weight; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setPeriodId(String periodId) { this.periodId = periodId; }
    public void setName(String name) { this.name = name; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
