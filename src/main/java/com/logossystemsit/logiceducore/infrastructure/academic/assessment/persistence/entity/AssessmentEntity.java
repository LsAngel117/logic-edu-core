package com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "assessments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "name"}))
public class AssessmentEntity {

    @Id
    private String id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "evaluation_period_id", nullable = true)
    private String evaluationPeriodId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "max_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getGroupId() { return groupId; }
    public String getEvaluationPeriodId() { return evaluationPeriodId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getMaxScore() { return maxScore; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setEvaluationPeriodId(String evaluationPeriodId) { this.evaluationPeriodId = evaluationPeriodId; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
