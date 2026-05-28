package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    @Test
    void shouldCreateRegisterRequestWithAllFields() {
        RegisterRequest request = new RegisterRequest(
                "jdoe",
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

        assertThat(request.username()).isEqualTo("jdoe");
        assertThat(request.email()).isEqualTo("john@example.com");
        assertThat(request.rawPassword()).isEqualTo("secretPassword");
        assertThat(request.firstGivenName()).isEqualTo("John");
        assertThat(request.secondGivenName()).isEqualTo("Michael");
        assertThat(request.firstFamilyName()).isEqualTo("Doe");
        assertThat(request.secondFamilyName()).isEqualTo("Smith");
        assertThat(request.sex()).isEqualTo("MALE");
        assertThat(request.birthDate()).isEqualTo("1990-05-15");
        assertThat(request.documentType()).isEqualTo("DNI");
        assertThat(request.documentValue()).isEqualTo("12345678");
        assertThat(request.role()).isEqualTo("STUDENT");
        assertThat(request.scopeType()).isEqualTo("SCHOOL");
        assertThat(request.scopeRefId()).isEqualTo("school-001");
    }

    @Test
    void shouldCreateRegisterRequestWithOptionalFieldsNull() {
        RegisterRequest request = new RegisterRequest(
                "jdoe",
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
                "STUDENT",
                "SCHOOL",
                "school-001"
        );

        assertThat(request.secondGivenName()).isNull();
        assertThat(request.secondFamilyName()).isNull();
        assertThat(request.firstGivenName()).isEqualTo("John");
    }
}
