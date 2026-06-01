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
                "STUDENT",
                "SCHOOL",
                "school-001"
        );

        assertThat(request.email()).isEqualTo("john@example.com");
        assertThat(request.rawPassword()).isEqualTo("secretPassword");
        assertThat(request.firstGivenName()).isEqualTo("John");
        assertThat(request.secondGivenName()).isEqualTo("Michael");
        assertThat(request.firstFamilyName()).isEqualTo("Doe");
        assertThat(request.role()).isEqualTo("STUDENT");
        assertThat(request.scopeType()).isEqualTo("SCHOOL");
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
                null
        );

        assertThat(request.secondGivenName()).isNull();
        assertThat(request.role()).isNull();
        assertThat(request.scopeType()).isNull();
    }
}
