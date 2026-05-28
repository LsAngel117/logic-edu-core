package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void shouldCreateAuthResponse() {
        AuthResponse response = new AuthResponse(
                "eyJhbGciOiJIUzI1NiJ9.xxx.yyy",
                "user-001",
                "jdoe"
        );

        assertThat(response.token()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.xxx.yyy");
        assertThat(response.userId()).isEqualTo("user-001");
        assertThat(response.username()).isEqualTo("jdoe");
    }
}
