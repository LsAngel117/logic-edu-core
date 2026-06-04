package com.logossystemsit.logiceducore.domain.academic.evaluation.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class EvaluationPeriod {

    private final EvaluationPeriodId id;
    private final AcademicPeriodId periodId;
    private final String name;
    private final int sequence;
    private final BigDecimal weight;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final EvaluationPeriodStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private EvaluationPeriod(
            EvaluationPeriodId id,
            AcademicPeriodId periodId,
            String name,
            int sequence,
            BigDecimal weight,
            LocalDate startDate,
            LocalDate endDate,
            EvaluationPeriodStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "EvaluationPeriodId is required");
        this.periodId = Objects.requireNonNull(periodId, "AcademicPeriodId is required");
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "name must not be blank");
        }
        this.name = name.trim();
        this.sequence = sequence;
        Objects.requireNonNull(weight, "weight is required");
        this.weight = weight;
        Objects.requireNonNull(startDate, "startDate is required");
        Objects.requireNonNull(endDate, "endDate is required");
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = Objects.requireNonNull(status, "status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */

    public static EvaluationPeriod create(
            EvaluationPeriodId id,
            AcademicPeriodId periodId,
            String name,
            int sequence,
            BigDecimal weight,
            LocalDate startDate,
            LocalDate endDate,
            Instant now
    ) {
        return new EvaluationPeriod(
                id, periodId, name, sequence, weight,
                startDate, endDate,
                EvaluationPeriodStatus.ACTIVE,
                now, now
        );
    }

    public static EvaluationPeriod restore(
            EvaluationPeriodId id,
            AcademicPeriodId periodId,
            String name,
            int sequence,
            BigDecimal weight,
            LocalDate startDate,
            LocalDate endDate,
            EvaluationPeriodStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new EvaluationPeriod(
                id, periodId, name, sequence, weight,
                startDate, endDate,
                status,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public EvaluationPeriod changeName(String newName, Instant now) {
        Objects.requireNonNull(newName, "name must not be null");
        if (newName.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "name must not be blank");
        }
        return new EvaluationPeriod(
                this.id, this.periodId, newName.trim(), this.sequence,
                this.weight,
                this.startDate, this.endDate,
                this.status,
                this.createdAt, now
        );
    }

    public EvaluationPeriod changeWeight(BigDecimal newWeight, Instant now) {
        Objects.requireNonNull(newWeight, "weight must not be null");
        return new EvaluationPeriod(
                this.id, this.periodId, this.name, this.sequence,
                newWeight,
                this.startDate, this.endDate,
                this.status,
                this.createdAt, now
        );
    }

    public EvaluationPeriod changeDates(LocalDate newStart, LocalDate newEnd, Instant now) {
        Objects.requireNonNull(newStart, "startDate is required");
        Objects.requireNonNull(newEnd, "endDate is required");
        return new EvaluationPeriod(
                this.id, this.periodId, this.name, this.sequence,
                this.weight,
                newStart, newEnd,
                this.status,
                this.createdAt, now
        );
    }

    public EvaluationPeriod deactivate(Instant now) {
        if (this.status == EvaluationPeriodStatus.INACTIVE) {
            return this;
        }
        return new EvaluationPeriod(
                this.id, this.periodId, this.name, this.sequence,
                this.weight,
                this.startDate, this.endDate,
                EvaluationPeriodStatus.INACTIVE,
                this.createdAt, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (!startDate.isBefore(endDate)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "start date must be before end date");
        }
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal max = new BigDecimal("100");
        if (weight.compareTo(zero) <= 0 || weight.compareTo(max) > 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "weight must be between 0 and 100");
        }
    }

    /* ---------- GETTERS ---------- */

    public EvaluationPeriodId getId() { return id; }
    public AcademicPeriodId getPeriodId() { return periodId; }
    public String getName() { return name; }
    public int getSequence() { return sequence; }
    public BigDecimal getWeight() { return weight; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public EvaluationPeriodStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
