package com.logossystemsit.logiceducore.domain.membership.model.valueobject;

import java.util.UUID;

public record MembershipId(String value) {

    public MembershipId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MembershipId is required");
        }
        value = value.trim();
    }

    public static MembershipId generate() {
        return new MembershipId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
