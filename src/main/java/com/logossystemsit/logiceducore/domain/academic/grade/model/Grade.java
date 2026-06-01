package com.logossystemsit.logiceducore.domain.academic.grade.model;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class Grade {

    private final GradeId id;
    private final AssessmentId assessmentId;
    private final UserId studentId;
    private final BigDecimal value;
    private final Instant gradedAt;
    private final Instant updatedAt;

    private Grade(
            GradeId id,
            AssessmentId assessmentId,
            UserId studentId,
            BigDecimal value,
            Instant gradedAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.assessmentId = Objects.requireNonNull(assessmentId, "assessmentId is required");
        this.studentId = Objects.requireNonNull(studentId, "studentId is required");

        Objects.requireNonNull(value, "value is required");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        this.value = value;

        this.gradedAt = Objects.requireNonNull(gradedAt, "gradedAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Grade create(
            GradeId id,
            AssessmentId assessmentId,
            UserId studentId,
            BigDecimal value,
            Instant now
    ) {
        return new Grade(id, assessmentId, studentId, value, now, now);
    }

    public static Grade restore(
            GradeId id,
            AssessmentId assessmentId,
            UserId studentId,
            BigDecimal value,
            Instant gradedAt,
            Instant updatedAt
    ) {
        return new Grade(id, assessmentId, studentId, value, gradedAt, updatedAt);
    }

    /* ---------- BEHAVIOR ---------- */

    public Grade changeValue(BigDecimal newValue, Instant now) {
        Objects.requireNonNull(newValue, "value is required");
        Objects.requireNonNull(now, "updatedAt is required");
        if (newValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        return new Grade(
                this.id, this.assessmentId, this.studentId,
                newValue,
                this.gradedAt, now
        );
    }

    /* ---------- EQUALS / HASHCODE ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ---------- GETTERS ---------- */

    public GradeId getId() { return id; }
    public AssessmentId getAssessmentId() { return assessmentId; }
    public UserId getStudentId() { return studentId; }
    public BigDecimal getValue() { return value; }
    public Instant getGradedAt() { return gradedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
