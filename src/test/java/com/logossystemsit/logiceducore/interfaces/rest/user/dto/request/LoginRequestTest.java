package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequest() {
        LoginRequest request = new LoginRequest("jdoe", "secretPassword");

        assertThat(request.username()).isEqualTo("jdoe");
        assertThat(request.rawPassword()).isEqualTo("secretPassword");
    }

    @Test
    void shouldCreateLoginRequestWithEmptyFields() {
        LoginRequest request = new LoginRequest("", "");

        assertThat(request.username()).isEmpty();
        assertThat(request.rawPassword()).isEmpty();
    }
}
