package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequest() {
        LoginRequest request = new LoginRequest("jdoe@example.com", "secretPassword");

        assertThat(request.email()).isEqualTo("jdoe@example.com");
        assertThat(request.rawPassword()).isEqualTo("secretPassword");
    }

    @Test
    void shouldCreateLoginRequestWithEmptyFields() {
        LoginRequest request = new LoginRequest("", "");

        assertThat(request.email()).isEmpty();
        assertThat(request.rawPassword()).isEmpty();
    }
}
