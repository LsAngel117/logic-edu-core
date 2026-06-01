package com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "grades",
       uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "student_id"}))
public class GradeEntity {

    @Id
    private String id;

    @Column(name = "assessment_id", nullable = false)
    private String assessmentId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "\"value\"", nullable = false, precision = 5, scale = 2)
    private BigDecimal value;

    @Column(name = "graded_at", nullable = false)
    private Instant gradedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getStudentId() { return studentId; }
    public BigDecimal getValue() { return value; }
    public Instant getGradedAt() { return gradedAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setGradedAt(Instant gradedAt) { this.gradedAt = gradedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
