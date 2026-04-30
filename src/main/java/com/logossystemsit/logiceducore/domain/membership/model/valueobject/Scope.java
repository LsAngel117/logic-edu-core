package com.logossystemsit.logiceducore.domain.membership.model.valueobject;

import java.util.Objects;
import java.util.Optional;

public final class Scope {

    public enum Type {
        PLATFORM,
        ACADEMY,
        SCHOOL,
        BRANCH,
        COURSE
    }

    private final Type type;
    private final String referenceId; // null solo para PLATFORM

    private Scope(Type type, String referenceId) {
        this.type = Objects.requireNonNull(type, "Scope type is required");
        this.referenceId = referenceId;
        validate();
    }

    public static Scope platform() {
        return new Scope(Type.PLATFORM, null);
    }

    public static Scope academy(String academyId) {
        return new Scope(Type.ACADEMY, normalizeId(academyId, "academyId"));
    }

    public static Scope school(String schoolId) {
        return new Scope(Type.SCHOOL, normalizeId(schoolId, "schoolId"));
    }

    public static Scope branch(String branchId) {
        return new Scope(Type.BRANCH, normalizeId(branchId, "branchId"));
    }

    public static Scope course(String courseId) {
        return new Scope(Type.COURSE, normalizeId(courseId, "courseId"));
    }

    public static Scope from(Type type, String referenceId) {
        return switch (type) {
            case PLATFORM -> platform();
            case ACADEMY -> academy(referenceId);
            case SCHOOL -> school(referenceId);
            case BRANCH ->  branch(referenceId);
            case COURSE -> course(referenceId);
        };
    }

    private static String normalizeId(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private void validate() {
        if (type == Type.PLATFORM && referenceId != null) {
            throw new IllegalArgumentException("PLATFORM scope must not have referenceId");
        }
        if (type != Type.PLATFORM && (referenceId == null || referenceId.isBlank())) {
            throw new IllegalArgumentException(type + " scope requires referenceId");
        }
    }

    public Type type() {
        return type;
    }

    public Optional<String> referenceId() {
        return Optional.ofNullable(referenceId);
    }

    public boolean isPlatform() {
        return type == Type.PLATFORM;
    }

    public boolean isSchool() {
        return type == Type.SCHOOL;
    }

    public boolean isBranch() {
        return type == Type.BRANCH;
    }

    public boolean isCourse() {
        return type == Type.COURSE;
    }

    public boolean isAcademy() {
        return type == Type.ACADEMY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scope scope)) return false;
        return type == scope.type && Objects.equals(referenceId, scope.referenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, referenceId);
    }

    @Override
    public String toString() {
        return type + (referenceId != null ? ":" + referenceId : "");
    }
}
