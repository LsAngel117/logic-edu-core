package com.logossystemsit.logiceducore.domain.academic.assessment.model;

import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class Assessment {

    private final AssessmentId id;
    private final GroupId groupId;
    private final EvaluationPeriodId evaluationPeriodId;
    private final String name;
    private final AssessmentType type;
    private final BigDecimal weight;
    private final BigDecimal maxScore;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Assessment(
            AssessmentId id,
            GroupId groupId,
            EvaluationPeriodId evaluationPeriodId,
            String name,
            AssessmentType type,
            BigDecimal weight,
            BigDecimal maxScore,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.groupId = Objects.requireNonNull(groupId, "groupId is required");
        this.evaluationPeriodId = evaluationPeriodId;

        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name.trim();

        this.type = Objects.requireNonNull(type, "type is required");

        Objects.requireNonNull(weight, "weight is required");
        if (weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("weight must be greater than zero");
        }
        this.weight = weight;

        Objects.requireNonNull(maxScore, "maxScore is required");
        if (maxScore.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("maxScore must be greater than zero");
        }
        this.maxScore = maxScore;

        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Assessment create(
            AssessmentId id,
            GroupId groupId,
            EvaluationPeriodId evaluationPeriodId,
            String name,
            AssessmentType type,
            BigDecimal weight,
            BigDecimal maxScore,
            Instant now
    ) {
        return new Assessment(id, groupId, evaluationPeriodId, name, type, weight, maxScore, now, now);
    }

    public static Assessment restore(
            AssessmentId id,
            GroupId groupId,
            EvaluationPeriodId evaluationPeriodId,
            String name,
            AssessmentType type,
            BigDecimal weight,
            BigDecimal maxScore,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Assessment(id, groupId, evaluationPeriodId, name, type, weight, maxScore, createdAt, updatedAt);
    }

    /* ---------- BEHAVIOR ---------- */

    public Assessment changeData(
            String newName,
            AssessmentType newType,
            BigDecimal newWeight,
            BigDecimal newMaxScore,
            EvaluationPeriodId newEvaluationPeriodId,
            Instant now
    ) {
        return new Assessment(
                this.id, this.groupId, newEvaluationPeriodId,
                newName, newType, newWeight, newMaxScore,
                this.createdAt, now
        );
    }

    /* ---------- EQUALS / HASHCODE ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assessment that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ---------- GETTERS ---------- */

    public AssessmentId getId() { return id; }
    public GroupId getGroupId() { return groupId; }
    public Optional<EvaluationPeriodId> getEvaluationPeriodId() { return Optional.ofNullable(evaluationPeriodId); }
    public String getName() { return name; }
    public AssessmentType getType() { return type; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getMaxScore() { return maxScore; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
