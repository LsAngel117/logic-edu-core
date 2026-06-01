package com.logossystemsit.logiceducore.domain.academic.group.model;

import java.util.UUID;

public record GroupId(String value) {

    public GroupId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GroupId is required");
        }
        value = value.trim();
    }

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID().toString());
    }
}
