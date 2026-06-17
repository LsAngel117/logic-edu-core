package com.logossystemsit.logiceducore.shared.valueobject;

import java.util.Objects;

public final class Address {
    private final String value;
    private static final int MAX_LENGTH = 255;

    private Address(String value) { this.value = normalize(value); }
    public static Address of(String value) { return new Address(value); }
    public static Address empty() { return new Address(null); }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        String n = value.trim().replaceAll("\\s+", " ");
        if (n.length() > MAX_LENGTH) throw new IllegalArgumentException("Address too long");
        return n;
    }

    public String value() { return value; }
    public boolean isPresent() { return value != null; }
    public boolean isEmpty() { return value == null; }

    @Override public boolean equals(Object o) { return o instanceof Address a && Objects.equals(value, a.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value == null ? "" : value; }
}
