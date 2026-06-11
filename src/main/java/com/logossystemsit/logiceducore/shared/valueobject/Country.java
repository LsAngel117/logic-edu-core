package com.logossystemsit.logiceducore.shared.valueobject;

public record Country(String value) {
    public Country {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Country is required");
        if (value.length() > 100)
            throw new IllegalArgumentException("Country too long");
    }
}
