package com.logossystemsit.logiceducore.domain.user.model;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;

import java.time.Instant;
import java.time.LocalDate;
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

    public User(UserId id,
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
        this.document = Objects.requireNonNull(document, "Document is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);

        validateBirthDate(birthDate);
        this.birthDate = birthDate;
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
        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                status,
                createdAt,
                updatedAt
        );
    }

    /* ---------- COMPORTAMIENTOS ---------- */
    public User block(Instant now) {
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
        Objects.requireNonNull(newPassword);

        if (this.status == Status.BLOCKED) {
            throw new IllegalStateException("Blocked user cannot change password");
        }

        return new User(
                id, username, email, newPassword, name, sex, birthDate, document,
                status,
                createdAt,
                now
        );
    }

    /* ---------- VALIDACIONES ---------- */
    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
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
