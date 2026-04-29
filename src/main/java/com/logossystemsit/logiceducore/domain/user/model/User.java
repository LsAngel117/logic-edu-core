package com.logossystemsit.logiceducore.domain.user.model;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

public class User {

    private final UserId id;
    private final Username username;
    private final Email email;
    private final PasswordHash passwordHash;
    private final Name name;
    private final Sex sex;
    private final LocalDate birthDate;
    private final Document document;
    private final Status status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(UserId id,
                Username username,
                Email email,
                PasswordHash passwordHash,
                Name name,
                Sex sex,
                LocalDate birthDate,
                Document document,
                Status status,
                Instant createdAt,
                Instant updatedAt ) {

        this.id = Objects.requireNonNull(id, "UserId is required");
        this.username = Objects.requireNonNull(username, "Username is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "PasswordHash is required");
        this.name = Objects.requireNonNull(name, "Name is required");
        this.sex = Objects.requireNonNull(sex, "Sex is required");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required");
        this.document = Objects.requireNonNull(document, "Document is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    /* ---------- FACTORY METHODS ---------- */
    public static User create(
            UserId id,
            Username username,
            Email email,
            PasswordHash passwordHash,
            Name name,
            Sex sex,
            LocalDate birthDate,
            Document document,
            Instant now
    ) {
        // Validación de regla de negocio: la fecha no puede ser futura
        validateBirthDate(birthDate, now);
        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                Status.ACTIVE, // controlado
                now,
                now
        );
    }

    public static User restore(
            UserId id,
            Username username,
            Email email,
            PasswordHash passwordHash,
            Name name,
            Sex sex,
            LocalDate birthDate,
            Document document,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {

        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(updatedAt);

        if (createdAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException("Invalid timestamps");
        }
        validateBirthDate(birthDate, updatedAt);

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                status,
                createdAt,
                updatedAt
        );
    }

    /* ---------- COMPORTAMIENTOS ---------- */
    public User block(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.BLOCKED) {
            throw new IllegalStateException("User already blocked");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                Status.BLOCKED,
                createdAt,
                now
        );
    }

    public User activate(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.ACTIVE) {
            throw new IllegalStateException("User already active");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                Status.ACTIVE,
                createdAt,
                now
        );
    }

    public User deactivate(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.INACTIVE) {
            throw new IllegalStateException("User already inactive");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                Status.INACTIVE,
                createdAt,
                now
        );
    }

    public User changePassword(PasswordHash newPassword, Instant now) {
        validateTimeProgression(now);
        Objects.requireNonNull(newPassword, "New password is required");
        ensurePasswordChangeAllowed(newPassword);

        return new User(
                id, username, email, newPassword, name, sex, birthDate, document,
                status,
                createdAt,
                now
        );
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    /* ---------- VALIDACIONES ---------- */
    private static void validateBirthDate(LocalDate birthDate, Instant now) {
        LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);

        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
    }

    private void validateTimeProgression(Instant now) {
        if (now.isBefore(this.updatedAt)) {
            throw new IllegalArgumentException("Invalid time progression");
        }
    }

    private void ensurePasswordChangeAllowed(PasswordHash newPassword) {
        if (this.status == Status.BLOCKED) {
            throw new IllegalStateException("Blocked user cannot change password");
        }

        if (this.passwordHash.equals(newPassword)) {
            throw new IllegalArgumentException("New password cannot be the same as the current one");
        }
    }

    /* ---------- ENUMS ---------- */
    public enum Status {
        ACTIVE, INACTIVE, BLOCKED
    }

    public enum Sex {
        MALE, FEMALE, OTHER
    }

    /* --- GETTERS --- */
    public UserId getId() { return id; }
    public Username getUsername() { return username; }
    public Email getEmail() { return email; }
    public PasswordHash getPasswordHash() { return passwordHash; }
    public Name getName() { return name; }
    public Sex getSex() { return sex; }
    public LocalDate getBirthDate() { return birthDate; }
    public Document getDocument() { return document; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
