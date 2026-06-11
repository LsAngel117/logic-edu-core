package com.logossystemsit.logiceducore.application.branch.dto.result;

import com.logossystemsit.logiceducore.domain.branch.model.Branch;

import java.time.Instant;

public record BranchResult(
        String id,
        String schoolId,
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address,
        String city,
        String country,
        String type,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static BranchResult from(Branch branch) {
        return new BranchResult(
                branch.getId().value(),
                branch.getSchoolId().value(),
                branch.getName().value(),
                branch.getCode().value(),
                branch.getShortName().value(),
                branch.getDescription().value().orElse(null),
                branch.getEmail() != null ? branch.getEmail().value() : null,
                branch.getPhone() != null ? branch.getPhone().value() : null,
                branch.getAddress().value().orElse(null),
                branch.getCity().value(),
                branch.getCountry().value(),
                branch.getType().name(),
                branch.getStatus().name(),
                branch.getCreatedAt(),
                branch.getUpdatedAt()
        );
    }
}
