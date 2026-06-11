package com.logossystemsit.logiceducore.shared.valueobject;

public record City(String value) {
    public City {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("City is required");
        if (value.length() > 100)
            throw new IllegalArgumentException("City too long");
    }
}
