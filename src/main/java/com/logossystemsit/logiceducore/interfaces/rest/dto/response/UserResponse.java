package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

public record UserResponse(
        String id,
        String username,
        String email,
        String fullName,
        String status,
        String createdAt
) {
}
