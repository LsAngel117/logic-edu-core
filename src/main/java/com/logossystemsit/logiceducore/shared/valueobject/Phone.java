package com.logossystemsit.logiceducore.shared.valueobject;

import java.util.Objects;

public final class Phone {
    private final String value;
    private static final int MAX_LENGTH = 20;

    private Phone(String value) { this.value = normalize(value); }
    public static Phone of(String value) { return new Phone(value); }
    public static Phone empty() { return new Phone(null); }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        String n = value.trim().replaceAll("\\s+", " ");
        if (n.length() > MAX_LENGTH) throw new IllegalArgumentException("Phone too long");
        return n;
    }

    public String value() { return value; }
    public boolean isPresent() { return value != null; }
    public boolean isEmpty() { return value == null; }

    @Override public boolean equals(Object o) { return o instanceof Phone p && Objects.equals(value, p.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value == null ? "" : value; }
}
