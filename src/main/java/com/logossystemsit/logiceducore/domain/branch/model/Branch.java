package com.logossystemsit.logiceducore.domain.branch.model;

import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.time.Instant;
import java.util.Objects;

public final class Branch {

    private final BranchId id;
    private final SchoolId schoolId;
    private final BranchName name;
    private final BranchCode code;
    private final BranchShortName shortName;
    private final BranchDescription description;
    private final BranchEmail email;
    private final BranchPhone phone;
    private final BranchAddress address;
    private final BranchType type;
    private final Status status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Branch(
            BranchId id,
            SchoolId schoolId,
            BranchName name,
            BranchCode code,
            BranchShortName shortName,
            BranchDescription description,
            BranchEmail email,
            BranchPhone phone,
            BranchAddress address,
            BranchType type,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "BranchId is required");
        this.schoolId = Objects.requireNonNull(schoolId, "SchoolId is required");
        this.name = Objects.requireNonNull(name, "BranchName is required");
        this.code = Objects.requireNonNull(code, "BranchCode is required");
        this.shortName = Objects.requireNonNull(shortName, "BranchShortName is required");

        this.description = description;
        this.email = email;
        this.phone = phone;
        this.address = address;

        this.type = Objects.requireNonNull(type, "BranchType is required");
        this.status = Objects.requireNonNull(status, "Status is required");

        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */
    public static Branch create(
            BranchId id,
            SchoolId schoolId,
            BranchName name,
            BranchCode code,
            BranchShortName shortName,
            BranchDescription description,
            BranchEmail email,
            BranchPhone phone,
            BranchAddress address,
            BranchType type,
            Instant now
    ) {
        return new Branch(
                id, schoolId, name, code, shortName, description, email, phone, address,
                type,
                Status.ACTIVE,
                now,
                now
        );
    }

    public static Branch restore(
            BranchId id,
            SchoolId schoolId,
            BranchName name,
            BranchCode code,
            BranchShortName shortName,
            BranchDescription description,
            BranchEmail email,
            BranchPhone phone,
            BranchAddress address,
            BranchType type,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Branch(
                id, schoolId, name, code, shortName, description, email, phone, address,
                type,
                status,
                createdAt,
                updatedAt
        );
    }

    /* ---------- COMPORTAMIENTOS ---------- */
    public Branch changeBasicInfo(
            BranchName name,
            BranchCode code,
            BranchShortName shortName,
            Instant now
    ) {
        ensureActive();

        return new Branch(
                this.id,
                this.schoolId,
                Objects.requireNonNull(name, "BranchName is required"),
                Objects.requireNonNull(code, "BranchCode is required"),
                Objects.requireNonNull(shortName, "BranchShortName is required"),
                this.description,
                this.email,
                this.phone,
                this.address,
                this.type,
                this.status,
                this.createdAt,
                now
        );
    }

    public Branch changeContactInfo(
            BranchDescription description,
            BranchEmail email,
            BranchPhone phone,
            Instant now
    ) {
        ensureActive();

        return new Branch(
                this.id, this.schoolId, this.name, this.code, this.shortName, description,
                email, phone, this.address, this.type,
                this.status,
                this.createdAt,
                now
        );
    }

    public Branch changeLocation(
            BranchAddress address,
            Instant now
    ) {
        ensureActive();

        return new Branch(
                this.id, this.schoolId, this.name, this.code, this.shortName, this.description,
                this.email, this.phone, address, this.type,
                this.status,
                this.createdAt,
                now
        );
    }

    public Branch changeType(
            BranchType type,
            Instant now
    ) {
        ensureActive();

        return new Branch(
                this.id, this.schoolId, this.name, this.code, this.shortName, this.description,
                this.email, this.phone, this.address,
                Objects.requireNonNull(type, "BranchType is required"),
                this.status,
                this.createdAt,
                now
        );
    }

    public Branch deactivate(Instant now) {
        if (this.status == Status.INACTIVE) {
            return this;
        }

        return new Branch(
                this.id, this.schoolId, this.name, this.code, this.shortName, this.description,
                this.email, this.phone, this.address, this.type,
                Status.INACTIVE,
                this.createdAt,
                now
        );
    }

    /* ---------- VALIDACIONES ---------- */
    private void validate() {
        //Sede física con dirección, virtual sin dirección
        if (type == BranchType.VIRTUAL && address != null && address.isPresent()) {
            throw new IllegalStateException("Virtual branch cannot have a physical address");
        }
        if (type == BranchType.MAIN || type == BranchType.SECONDARY) {
            if (address == null || address.isEmpty()) {
                throw new IllegalStateException("Physical branch must have an address");
            }
        }
    }

    private void ensureActive() {
        if (this.status == Status.INACTIVE) {
            throw new IllegalStateException("Cannot modify an inactive branch");
        }
    }

    /* ---------- AUXILIARES ---------- */
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isMain() {
        return type == BranchType.MAIN;
    }

    public boolean isSecondary() {
        return type == BranchType.SECONDARY;
    }

    public boolean isVirtual() {
        return type == BranchType.VIRTUAL;
    }

    /* ---------- ENUMS ---------- */
    public enum Status { ACTIVE, INACTIVE }

    /* ---------- GETTERS ---------- */
    public BranchId getId() { return id; }
    public SchoolId getSchoolId() { return schoolId; }
    public BranchName getName() { return name; }
    public BranchCode getCode() { return code; }
    public BranchShortName getShortName() { return shortName; }
    public BranchDescription getDescription() { return description; }
    public BranchEmail getEmail() { return email; }
    public BranchPhone getPhone() { return phone; }
    public BranchAddress getAddress() { return address; }
    public BranchType getType() { return type; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
