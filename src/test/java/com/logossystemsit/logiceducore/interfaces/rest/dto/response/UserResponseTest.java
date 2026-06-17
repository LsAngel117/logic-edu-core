package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void shouldCreateUserResponse() {
        UserResponse response = new UserResponse(
                "user-001",
                "jdoe",
                "john@example.com",
                "John Doe",
                "ACTIVE",
                "2026-01-15T10:30:00Z",
                null, null, null, null
        );

        assertThat(response.id()).isEqualTo("user-001");
        assertThat(response.username()).isEqualTo("jdoe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.createdAt()).isEqualTo("2026-01-15T10:30:00Z");
    }

    @Test
    void shouldCreateUserResponseWithInactiveStatus() {
        UserResponse response = new UserResponse(
                "user-002",
                "asmith",
                "anna@example.com",
                "Anna Smith",
                "INACTIVE",
                "2026-02-01T08:00:00Z",
                null, null, null, null
        );

        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.username()).isEqualTo("asmith");
    }
}
