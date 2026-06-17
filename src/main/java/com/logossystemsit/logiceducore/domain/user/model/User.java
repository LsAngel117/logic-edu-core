package com.logossystemsit.logiceducore.domain.user.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.valueobject.*;

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
    private final Phone phone;
    private final Address address;
    private final City city;
    private final Country country;
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
                Phone phone,
                Address address,
                City city,
                Country country,
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
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
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
                null, null, null, null,
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
            Phone phone,
            Address address,
            City city,
            Country country,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {

        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(updatedAt);

        if (createdAt.isAfter(updatedAt)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Invalid timestamps");
        }
        validateBirthDate(birthDate, updatedAt);

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country,
                status,
                createdAt,
                updatedAt
        );
    }

    /* ---------- COMPORTAMIENTOS ---------- */
    public User block(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.BLOCKED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "User already blocked");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country,
                Status.BLOCKED,
                createdAt,
                now
        );
    }

    public User activate(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.ACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "User already active");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country,
                Status.ACTIVE,
                createdAt,
                now
        );
    }

    public User deactivate(Instant now) {
        validateTimeProgression(now);
        if (this.status == Status.INACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "User already inactive");
        }

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country,
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
                phone, address, city, country,
                status,
                createdAt,
                now
        );
    }

    public User changeBasicInfo(Email email, Name name, User.Sex sex,
                                LocalDate birthDate, Document document, Instant now,
                                Phone phone, Address address, City city, Country country) {
        validateTimeProgression(now);
        ensureNotBlocked();
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(sex, "Sex is required");
        Objects.requireNonNull(birthDate, "Birth date is required");
        Objects.requireNonNull(document, "Document is required");
        validateBirthDate(birthDate, now);

        return new User(
                id, username, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country,
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
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Birth date cannot be in the future");
        }
    }

    private void validateTimeProgression(Instant now) {
        if (now.isBefore(this.updatedAt)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Invalid time progression");
        }
    }

    private void ensurePasswordChangeAllowed(PasswordHash newPassword) {
        if (this.status == Status.BLOCKED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Blocked user cannot change password");
        }

        if (this.passwordHash.equals(newPassword)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "New password cannot be the same as the current one");
        }
    }

    private void ensureNotBlocked() {
        if (this.status == Status.BLOCKED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Blocked user cannot be modified");
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
    public Phone getPhone() { return phone; }
    public Address getAddress() { return address; }
    public City getCity() { return city; }
    public Country getCountry() { return country; }
}
