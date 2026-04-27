package com.logossystemsit.logiceducore.domain.model;

import java.util.Locale;
import java.util.UUID;

public class User {

    private final String id;
    private final String username;
    private final String passwordHash;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;
    private final Status status;

    public User(String username, String passwordHash, String email,
                String firstName, String lastName, Role role, Status status) {

        String normalizedUsername = normalizeUsername(username);
        String normalizedEmail = normalizeEmail(email);

        validateUsername(normalizedUsername);
        validateEmail(normalizedEmail);
        validatePasswordHash(passwordHash);
        validateNames(firstName, lastName);
        validateRole(role);
        validateStatus(status);

        this.id = UUID.randomUUID().toString();
        this.username = normalizedUsername;
        this.passwordHash = passwordHash;
        this.email = normalizedEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
    }

    /* --- Normalizaciones --- */
    private String normalizeUsername(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /* --- Validaciones --- */
    private void validateUsername(String username) {
        if (username == null || !username.matches("^[a-z0-9._-]{3,20}$")) {
            throw new IllegalArgumentException("Invalid username format");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    private void validatePasswordHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
    }

    private void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    private void validateStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
    }

    /* --- ENUMS --- */
    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    public enum Status {
        ACTIVE, INACTIVE, BLOCKED
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
}
