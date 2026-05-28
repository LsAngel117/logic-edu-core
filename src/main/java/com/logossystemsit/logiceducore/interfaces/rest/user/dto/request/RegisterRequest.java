package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

public record RegisterRequest(
        String username,
        String email,
        String rawPassword,
        String firstGivenName,
        String secondGivenName,
        String firstFamilyName,
        String secondFamilyName,
        String sex,
        String birthDate,
        String documentType,
        String documentValue,
        String role,
        String scopeType,
        String scopeRefId
) {
}
