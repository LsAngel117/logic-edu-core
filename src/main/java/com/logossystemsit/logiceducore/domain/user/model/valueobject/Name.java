package com.logossystemsit.logiceducore.domain.user.model.valueobject;

import java.util.Objects;
import java.util.Set;

public class Name {

    private final String firstGivenName;
    private final String secondGivenName;   // opcional
    private final String firstFamilyName;
    private final String secondFamilyName;  // opcional
    private static final String NAME_REGEX =
            "^[A-Za-zÁÉÍÓÚáéíóúÑñ'\\- ]+$";
    private static final Set<String> NAME_PARTICLES = Set.of(
            "de", "del", "la", "las", "los", "da", "dos", "do"
    );

    public Name(String firstGivenName,
                String secondGivenName,
                String firstFamilyName,
                String secondFamilyName) {

        this.firstGivenName = normalizeRequired(firstGivenName, "First given name is required");
        this.firstFamilyName = normalizeRequired(firstFamilyName, "First family name is required");

        this.secondGivenName = normalizeOptional(secondGivenName);
        this.secondFamilyName = normalizeOptional(secondFamilyName);
    }

    private void validateLength(String value) {
        if (value.length() > 50) {
            throw new IllegalArgumentException("Name too long");
        }
    }

    private void validateFormat(String value) {
        if (!value.matches(NAME_REGEX)) {
            throw new IllegalArgumentException("Invalid name format");
        }
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        String normalized = normalize(value);
        normalized = capitalize(normalized);

        validateLength(normalized);
        validateFormat(normalized);

        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) return null;

        String normalized = normalize(value);
        normalized = capitalize(normalized);

        validateLength(normalized);
        validateFormat(normalized);

        return normalized;
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String capitalize(String value) {
        String[] parts = value.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.isEmpty()) continue;

            if (i > 0 && NAME_PARTICLES.contains(part)) {
                result.append(part);
            } else {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1));
            }
            result.append(" ");
        }
        return result.toString().trim();
    }

    public String getFullName() {
        return String.join(" ",
                firstGivenName,
                secondGivenName != null ? secondGivenName : "",
                firstFamilyName,
                secondFamilyName != null ? secondFamilyName : ""
        ).trim().replaceAll("\\s+", " ");
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;
        Name name = (Name) o;
        return Objects.equals(firstGivenName, name.firstGivenName) &&
                Objects.equals(secondGivenName, name.secondGivenName) &&
                Objects.equals(firstFamilyName, name.firstFamilyName) &&
                Objects.equals(secondFamilyName, name.secondFamilyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstGivenName, secondGivenName, firstFamilyName, secondFamilyName);
    }

    public String getFirstGivenName() {
        return firstGivenName;
    }

    public String getSecondGivenName() {
        return secondGivenName;
    }

    public String getFirstFamilyName() {
        return firstFamilyName;
    }

    public String getSecondFamilyName() {
        return secondFamilyName;
    }
}
