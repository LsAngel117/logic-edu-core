package com.logossystemsit.logiceducore.interfaces.rest.branch.dto.response;

public record BranchResponse(
        String id,
        String schoolId,
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address,
        String type,
        String status,
        String createdAt,
        String updatedAt
) {}
