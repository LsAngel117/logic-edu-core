package com.logossystemsit.logiceducore.interfaces.rest.school.dto.request;

public record CreateSchoolRequest(
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address
) {}
