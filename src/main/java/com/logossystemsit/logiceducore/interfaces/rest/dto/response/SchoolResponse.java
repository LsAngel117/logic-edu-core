package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

public record SchoolResponse(
        String id,
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address,
        String status,
        String createdAt,
        String updatedAt
) {}
