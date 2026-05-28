package com.logossystemsit.logiceducore.infrastructure.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JwtPropertiesTest.Config.class)
@TestPropertySource(properties = {
        "jwt.secret=test-secret-value",
        "jwt.expiration-ms=7200000"
})
class JwtPropertiesTest {

    @Autowired
    private JwtProperties properties;

    @Configuration
    @EnableConfigurationProperties(JwtProperties.class)
    static class Config {
    }

    @Test
    void shouldBindSecretFromConfiguration() {
        assertThat(properties.secret()).isEqualTo("test-secret-value");
    }

    @Test
    void shouldBindExpirationMsFromConfiguration() {
        assertThat(properties.expirationMs()).isEqualTo(7200000L);
    }
}
