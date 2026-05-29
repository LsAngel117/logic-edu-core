package com.logossystemsit.logiceducore.application.school.dto.result;

import com.logossystemsit.logiceducore.domain.school.model.School;

import java.time.Instant;

public record SchoolResult(
        String id,
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SchoolResult from(School school) {
        return new SchoolResult(
                school.getId().value(),
                school.getName().value(),
                school.getCode().value(),
                school.getShortName().value(),
                school.getDescription().value().orElse(null),
                school.getEmail() != null ? school.getEmail().value() : null,
                school.getPhone() != null ? school.getPhone().value() : null,
                school.getAddress().value().orElse(null),
                school.getStatus().name(),
                school.getCreatedAt(),
                school.getUpdatedAt()
        );
    }
}
