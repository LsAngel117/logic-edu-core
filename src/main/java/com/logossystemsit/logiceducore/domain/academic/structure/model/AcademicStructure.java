package com.logossystemsit.logiceducore.domain.academic.structure.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.time.Instant;
import java.util.Objects;

public final class AcademicStructure {

    private final AcademicStructureId id;
    private final SchoolId schoolId;
    private final StructureType structureType;
    private final int levelsCount;
    private final int periodsPerLevel;
    private final int evaluationPeriodsPerPeriod;
    private final int subjectsPerPeriod;
    private final int hoursPerSubject;
    private final boolean active;
    private final int version;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AcademicStructure(
            AcademicStructureId id,
            SchoolId schoolId,
            StructureType structureType,
            int levelsCount,
            int periodsPerLevel,
            int evaluationPeriodsPerPeriod,
            int subjectsPerPeriod,
            int hoursPerSubject,
            int version,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "AcademicStructureId is required");
        this.schoolId = Objects.requireNonNull(schoolId, "SchoolId is required");
        this.structureType = Objects.requireNonNull(structureType, "StructureType is required");
        this.levelsCount = levelsCount;
        this.periodsPerLevel = periodsPerLevel;
        this.evaluationPeriodsPerPeriod = evaluationPeriodsPerPeriod;
        this.subjectsPerPeriod = subjectsPerPeriod;
        this.hoursPerSubject = hoursPerSubject;
        this.version = version;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */

    public static AcademicStructure create(
            AcademicStructureId id,
            SchoolId schoolId,
            StructureType structureType,
            int levelsCount,
            int periodsPerLevel,
            int evaluationPeriodsPerPeriod,
            int subjectsPerPeriod,
            int hoursPerSubject,
            Instant now
    ) {
        return new AcademicStructure(
                id, schoolId, structureType,
                levelsCount, periodsPerLevel, evaluationPeriodsPerPeriod,
                subjectsPerPeriod, hoursPerSubject,
                1, true,
                now, now
        );
    }

    public static AcademicStructure restore(
            AcademicStructureId id,
            SchoolId schoolId,
            StructureType structureType,
            int levelsCount,
            int periodsPerLevel,
            int evaluationPeriodsPerPeriod,
            int subjectsPerPeriod,
            int hoursPerSubject,
            int version,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new AcademicStructure(
                id, schoolId, structureType,
                levelsCount, periodsPerLevel, evaluationPeriodsPerPeriod,
                subjectsPerPeriod, hoursPerSubject,
                version, active,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public AcademicStructure deactivate(Instant now) {
        if (!this.active) {
            return this;
        }

        return new AcademicStructure(
                this.id, this.schoolId, this.structureType,
                this.levelsCount, this.periodsPerLevel, this.evaluationPeriodsPerPeriod,
                this.subjectsPerPeriod, this.hoursPerSubject,
                this.version, false,
                this.createdAt, now
        );
    }

    public AcademicStructure changeVersion(
            StructureType structureType,
            int levelsCount,
            int periodsPerLevel,
            int evaluationPeriodsPerPeriod,
            int subjectsPerPeriod,
            int hoursPerSubject,
            Instant now
    ) {
        return new AcademicStructure(
                AcademicStructureId.generate(),
                this.schoolId,
                Objects.requireNonNull(structureType, "StructureType is required"),
                levelsCount, periodsPerLevel, evaluationPeriodsPerPeriod,
                subjectsPerPeriod, hoursPerSubject,
                this.version + 1, true,
                now, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (levelsCount < 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "levelsCount must be non-negative");
        }
        if (periodsPerLevel < 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "periodsPerLevel must be non-negative");
        }
        if (evaluationPeriodsPerPeriod < 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "evaluationPeriodsPerPeriod must be non-negative");
        }
        if (subjectsPerPeriod < 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "subjectsPerPeriod must be non-negative");
        }
        if (hoursPerSubject < 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "hoursPerSubject must be non-negative");
        }
    }

    /* ---------- GETTERS ---------- */

    public AcademicStructureId getId() { return id; }
    public SchoolId getSchoolId() { return schoolId; }
    public StructureType getStructureType() { return structureType; }
    public int getLevelsCount() { return levelsCount; }
    public int getPeriodsPerLevel() { return periodsPerLevel; }
    public int getEvaluationPeriodsPerPeriod() { return evaluationPeriodsPerPeriod; }
    public int getSubjectsPerPeriod() { return subjectsPerPeriod; }
    public int getHoursPerSubject() { return hoursPerSubject; }
    public boolean isActive() { return active; }
    public int getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
