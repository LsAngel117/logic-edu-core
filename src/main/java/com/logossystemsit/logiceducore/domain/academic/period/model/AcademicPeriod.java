package com.logossystemsit.logiceducore.domain.academic.period.model;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class AcademicPeriod {

    private final AcademicPeriodId id;
    private final AcademicLevelId levelId;
    private final PeriodType periodType;
    private final String name;
    private final int sequence;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PeriodStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AcademicPeriod(
            AcademicPeriodId id,
            AcademicLevelId levelId,
            PeriodType periodType,
            String name,
            int sequence,
            LocalDate startDate,
            LocalDate endDate,
            PeriodStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "AcademicPeriodId is required");
        this.levelId = Objects.requireNonNull(levelId, "AcademicLevelId is required");
        this.periodType = Objects.requireNonNull(periodType, "periodType is required");
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name.trim();
        this.sequence = sequence;
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

    public static AcademicPeriod create(
            AcademicPeriodId id,
            AcademicLevelId levelId,
            PeriodType periodType,
            String name,
            int sequence,
            LocalDate startDate,
            LocalDate endDate,
            Instant now
    ) {
        return new AcademicPeriod(
                id, levelId, periodType, name, sequence,
                startDate, endDate,
                PeriodStatus.ACTIVE,
                now, now
        );
    }

    public static AcademicPeriod restore(
            AcademicPeriodId id,
            AcademicLevelId levelId,
            PeriodType periodType,
            String name,
            int sequence,
            LocalDate startDate,
            LocalDate endDate,
            PeriodStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new AcademicPeriod(
                id, levelId, periodType, name, sequence,
                startDate, endDate,
                status,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public AcademicPeriod changeDates(LocalDate newStart, LocalDate newEnd, Instant now) {
        Objects.requireNonNull(newStart, "startDate is required");
        Objects.requireNonNull(newEnd, "endDate is required");
        if (!newStart.isBefore(newEnd)) {
            throw new IllegalArgumentException("start date must be before end date");
        }

        return new AcademicPeriod(
                this.id, this.levelId, this.periodType, this.name, this.sequence,
                newStart, newEnd,
                this.status,
                this.createdAt, now
        );
    }

    public AcademicPeriod changeName(String newName, Instant now) {
        Objects.requireNonNull(newName, "name must not be null");
        if (newName.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        return new AcademicPeriod(
                this.id, this.levelId, this.periodType, newName.trim(), this.sequence,
                this.startDate, this.endDate,
                this.status,
                this.createdAt, now
        );
    }

    public AcademicPeriod deactivate(Instant now) {
        if (this.status == PeriodStatus.INACTIVE) {
            return this;
        }

        return new AcademicPeriod(
                this.id, this.levelId, this.periodType, this.name, this.sequence,
                this.startDate, this.endDate,
                PeriodStatus.INACTIVE,
                this.createdAt, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("start date must be before end date");
        }
    }

    /* ---------- GETTERS ---------- */

    public AcademicPeriodId getId() { return id; }
    public AcademicLevelId getLevelId() { return levelId; }
    public PeriodType getPeriodType() { return periodType; }
    public String getName() { return name; }
    public int getSequence() { return sequence; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public PeriodStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
