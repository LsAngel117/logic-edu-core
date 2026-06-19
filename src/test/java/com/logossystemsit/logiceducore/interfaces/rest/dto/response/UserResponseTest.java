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
                "MALE",
                "1990-05-15",
                "CC",
                "1234567890",
                "2026-01-15T10:30:00Z",
                "+573001234567",
                "Calle 123",
                "Medellín",
                "Colombia"
        );

        assertThat(response.id()).isEqualTo("user-001");
        assertThat(response.username()).isEqualTo("jdoe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.documentType()).isEqualTo("CC");
        assertThat(response.documentValue()).isEqualTo("1234567890");
        assertThat(response.createdAt()).isEqualTo("2026-01-15T10:30:00Z");
        assertThat(response.city()).isEqualTo("Medellín");
    }

    @Test
    void shouldCreateUserResponseWithInactiveStatus() {
        UserResponse response = new UserResponse(
                "user-002",
                "asmith",
                "anna@example.com",
                "Anna Smith",
                "INACTIVE",
                "FEMALE",
                "1995-03-20",
                "TI",
                "9876543210",
                "2026-02-01T08:00:00Z",
                null, null, null, null
        );

        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.username()).isEqualTo("asmith");
        assertThat(response.documentType()).isEqualTo("TI");
    }
}
