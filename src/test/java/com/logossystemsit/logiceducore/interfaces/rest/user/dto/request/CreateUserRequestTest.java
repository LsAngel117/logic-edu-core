package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    @Test
    void shouldCreateCreateUserRequestWithAllFields() {
        CreateUserRequest request = new CreateUserRequest(
                "john@example.com",
                "secretPassword",
                "John",
                "Michael",
                "Doe",
                "Smith",
                "MALE",
                "1990-05-15",
                "DNI",
                "12345678",
                "+573001234567",
                "Calle 123",
                "Medellín",
                "Colombia",
                "STUDENT",
                "SCHOOL",
                "school-001"
        );

        assertThat(request.email()).isEqualTo("john@example.com");
        assertThat(request.phone()).isEqualTo("+573001234567");
        assertThat(request.city()).isEqualTo("Medellín");
        assertThat(request.country()).isEqualTo("Colombia");
        assertThat(request.role()).isEqualTo("STUDENT");
    }

    @Test
    void shouldCreateCreateUserRequestWithNullableFieldsNull() {
        CreateUserRequest request = new CreateUserRequest(
                "john@example.com",
                "secretPassword",
                "John",
                null,
                "Doe",
                null,
                "MALE",
                "1990-05-15",
                "DNI",
                "12345678",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(request.phone()).isNull();
        assertThat(request.city()).isNull();
    }
}
