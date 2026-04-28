package com.logossystemsit.logiceducore.domain.user.model.valueobject;

public class DocumentNumber {

    private final String value;

    public DocumentNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Document number is required");
        }

        String normalized = value.trim();

        if (!normalized.matches("^[0-9A-Za-z]+$")) {
            throw new IllegalArgumentException("Invalid document format");
        }

        this.value = normalized;
    }

    public String getValue() {
        return value;
    }
}
