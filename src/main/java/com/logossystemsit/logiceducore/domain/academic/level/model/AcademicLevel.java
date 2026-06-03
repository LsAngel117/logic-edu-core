package com.logossystemsit.logiceducore.domain.academic.level.model;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.time.Instant;
import java.util.Objects;

public final class AcademicLevel {

    private final AcademicLevelId id;
    private final SchoolId schoolId;
    private final String name;
    private final int number;
    private final AcademicLevelStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AcademicLevel(
            AcademicLevelId id,
            SchoolId schoolId,
            String name,
            int number,
            AcademicLevelStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "AcademicLevelId is required");
        this.schoolId = Objects.requireNonNull(schoolId, "SchoolId is required");
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name.trim();
        this.number = number;
        this.status = Objects.requireNonNull(status, "status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */

    public static AcademicLevel create(
            AcademicLevelId id,
            SchoolId schoolId,
            String name,
            int number,
            Instant now
    ) {
        return new AcademicLevel(
                id, schoolId, name, number,
                AcademicLevelStatus.ACTIVE,
                now, now
        );
    }

    public static AcademicLevel restore(
            AcademicLevelId id,
            SchoolId schoolId,
            String name,
            int number,
            AcademicLevelStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new AcademicLevel(
                id, schoolId, name, number,
                status,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public AcademicLevel changeName(String newName, Instant now) {
        Objects.requireNonNull(newName, "name must not be null");
        if (newName.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        return new AcademicLevel(
                this.id, this.schoolId, newName.trim(), this.number,
                this.status,
                this.createdAt, now
        );
    }

    public AcademicLevel changeNumber(int newNumber, Instant now) {
        return new AcademicLevel(
                this.id, this.schoolId, this.name, newNumber,
                this.status,
                this.createdAt, now
        );
    }

    public AcademicLevel deactivate(Instant now) {
        if (this.status == AcademicLevelStatus.INACTIVE) {
            return this;
        }

        return new AcademicLevel(
                this.id, this.schoolId, this.name, this.number,
                AcademicLevelStatus.INACTIVE,
                this.createdAt, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (number <= 0) {
            throw new IllegalArgumentException("number must be positive");
        }
    }

    /* ---------- GETTERS ---------- */

    public AcademicLevelId getId() { return id; }
    public SchoolId getSchoolId() { return schoolId; }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public AcademicLevelStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
