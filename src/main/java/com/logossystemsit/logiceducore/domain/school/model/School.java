package com.logossystemsit.logiceducore.domain.school.model;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;

import java.time.Instant;
import java.util.Objects;

public final class School {

    private final SchoolId id;
    private final SchoolName name;
    private final SchoolCode code;
    private final SchoolShortName shortName;
    private final SchoolDescription description;
    private final SchoolEmail email;
    private final SchoolPhone phone;
    private final SchoolAddress address;
    private final Status status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private School(
            SchoolId id,
            SchoolName name,
            SchoolCode code,
            SchoolShortName shortName,
            SchoolDescription description,
            SchoolEmail email,
            SchoolPhone phone,
            SchoolAddress address,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "SchoolId is required");
        this.name = Objects.requireNonNull(name, "SchoolName is required");
        this.code = Objects.requireNonNull(code, "SchoolCode is required");
        this.shortName = Objects.requireNonNull(shortName, "SchoolShortName is required");

        this.description = description;
        this.email = email;
        this.phone = phone;
        this.address = address;

        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */
    public static School create(
            SchoolId id,
            SchoolName name,
            SchoolCode code,
            SchoolShortName shortName,
            SchoolDescription description,
            SchoolEmail email,
            SchoolPhone phone,
            SchoolAddress address,
            Instant now
    ) {
        return new School(
                id, name, code, shortName, description, email, phone, address,
                Status.ACTIVE,
                now,
                now
        );
    }

    public static School restore(
            SchoolId id,
            SchoolName name,
            SchoolCode code,
            SchoolShortName shortName,
            SchoolDescription description,
            SchoolEmail email,
            SchoolPhone phone,
            SchoolAddress address,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new School(
                id, name, code, shortName, description, email, phone, address,
                status,
                createdAt,
                updatedAt
        );
    }

    /* ---------- COMPORTAMIENTOS ---------- */
    public School changeData(
            SchoolName name,
            SchoolCode code,
            SchoolShortName shortName,
            SchoolDescription description,
            SchoolEmail email,
            SchoolPhone phone,
            SchoolAddress address,
            Instant now
    ) {
        ensureActive();

        return new School(
                this.id,
                Objects.requireNonNull(name, "SchoolName is required"),
                Objects.requireNonNull(code, "SchoolCode is required"),
                Objects.requireNonNull(shortName, "SchoolShortName is required"),
                description,
                email,
                phone,
                address,
                this.status,
                this.createdAt,
                now
        );
    }

    public School deactivate(Instant now) {
        if (this.status == Status.INACTIVE) {
            return this;
        }

        return new School(
                this.id, this.name, this.code, this.shortName, this.description, this.email,
                this.phone,
                this.address,
                Status.INACTIVE,
                this.createdAt,
                now
        );
    }

    /* ---------- VALIDACIONES ---------- */
    private void validate() {
        // validar coherencias futuras aquí
    }

    /* ---------- AUXILIARES ---------- */
    private void ensureActive() {
        if (this.status == Status.INACTIVE) {
            throw new IllegalStateException("Cannot modify an inactive school");
        }
    }

    /* ---------- ENUMS ---------- */
    public enum Status { ACTIVE, INACTIVE }

    /* --- GETTERS --- */
    public SchoolId getId() { return id; }
    public SchoolName getName() {  return name; }
    public SchoolCode getCode() { return code;     }
    public SchoolShortName getShortName() { return shortName; }
    public SchoolDescription getDescription() { return description; }
    public SchoolEmail getEmail() { return email; }
    public SchoolPhone getPhone() { return phone; }
    public SchoolAddress getAddress() { return address; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public boolean isActive() {
        return status == Status.ACTIVE;
    }
}
