package com.logossystemsit.logiceducore.domain.academic.subject.model;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.time.Instant;
import java.util.Objects;

public final class Subject {

    private final SubjectId id;
    private final SchoolId schoolId;
    private final String code;
    private final String name;
    private final String description;
    private final int hours;
    private final SubjectStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Subject(
            SubjectId id,
            SchoolId schoolId,
            String code,
            String name,
            String description,
            int hours,
            SubjectStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "SubjectId is required");
        this.schoolId = Objects.requireNonNull(schoolId, "SchoolId is required");
        Objects.requireNonNull(code, "code is required");
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        this.code = code.trim();
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.hours = hours;
        this.status = Objects.requireNonNull(status, "status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Subject create(
            SubjectId id,
            SchoolId schoolId,
            String code,
            String name,
            String description,
            int hours,
            Instant now
    ) {
        return new Subject(
                id, schoolId, code, name, description, hours,
                SubjectStatus.ACTIVE,
                now, now
        );
    }

    public static Subject restore(
            SubjectId id,
            SchoolId schoolId,
            String code,
            String name,
            String description,
            int hours,
            SubjectStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Subject(
                id, schoolId, code, name, description, hours,
                status,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public Subject changeData(String newCode, String newName, String newDescription, int newHours, Instant now) {
        Objects.requireNonNull(newCode, "code must not be null");
        if (newCode.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        Objects.requireNonNull(newName, "name must not be null");
        if (newName.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (newHours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }

        return new Subject(
                this.id, this.schoolId, newCode.trim(), newName.trim(),
                newDescription != null ? newDescription.trim() : null,
                newHours,
                this.status,
                this.createdAt, now
        );
    }

    public Subject deactivate(Instant now) {
        if (this.status == SubjectStatus.INACTIVE) {
            return this;
        }

        return new Subject(
                this.id, this.schoolId, this.code, this.name, this.description, this.hours,
                SubjectStatus.INACTIVE,
                this.createdAt, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (hours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }
    }

    /* ---------- GETTERS ---------- */

    public SubjectId getId() { return id; }
    public SchoolId getSchoolId() { return schoolId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getHours() { return hours; }
    public SubjectStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
