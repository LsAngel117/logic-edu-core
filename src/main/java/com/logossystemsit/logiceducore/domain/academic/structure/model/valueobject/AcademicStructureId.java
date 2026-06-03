package com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject;

import java.util.UUID;

public record AcademicStructureId(String value) {

    public AcademicStructureId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AcademicStructureId is required");
        }
        value = value.trim();
    }

    public static AcademicStructureId generate() {
        return new AcademicStructureId(UUID.randomUUID().toString());
    }
}
