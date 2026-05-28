package com.logossystemsit.logiceducore.infrastructure.security.config;

import com.logossystemsit.logiceducore.infrastructure.security.filter.JwtAuthenticationFilter;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtService jwtService;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtService);
    }

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        assertThat(encoder).isNotNull();
        String raw = "myPassword";
        String encoded = encoder.encode(raw);
        assertThat(encoded).isNotEqualTo(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    void jwtAuthenticationFilter_shouldReturnConfiguredFilter() {
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();

        assertThat(filter).isNotNull();
    }
}
