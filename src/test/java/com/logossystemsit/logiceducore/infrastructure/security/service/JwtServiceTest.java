package com.logossystemsit.logiceducore.infrastructure.security.service;

import com.logossystemsit.logiceducore.infrastructure.security.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService service;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(
                "my-test-secret-key-that-is-long-enough-for-hmac-sha256!!",
                3600000L // 1 hour
        );
        service = new JwtService(props);
    }

    @Test
    void generate_shouldProduceNonEmptyToken() {
        String token = service.generate("user-123", List.of());

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void generate_shouldProduceTokenThatValidatesSuccessfully() {
        String token = service.generate("user-456", List.of());

        boolean valid = service.validate(token);

        assertThat(valid).isTrue();
    }

    @Test
    void validate_shouldRejectTamperedToken() {
        String token = service.generate("user-789", List.of());
        String tampered = token.substring(0, token.length() - 4) + "XXXX";

        boolean valid = service.validate(tampered);

        assertThat(valid).isFalse();
    }

    @Test
    void extractUserId_shouldReturnSubjectClaim() {
        String token = service.generate("user-abc", List.of());

        String userId = service.extractUserId(token);

        assertThat(userId).isEqualTo("user-abc");
    }

    @Test
    void extractMemberships_shouldReturnEmbeddedClaims() {
        List<MembershipClaim> memberships = List.of(
                new MembershipClaim("TEACHER", "COURSE", "course-1"),
                new MembershipClaim("STUDENT", "COURSE", "course-2")
        );
        String token = service.generate("user-def", memberships);

        List<MembershipClaim> extracted = service.extractMemberships(token);

        assertThat(extracted).hasSize(2);
        assertThat(extracted.get(0).role()).isEqualTo("TEACHER");
        assertThat(extracted.get(0).scopeType()).isEqualTo("COURSE");
        assertThat(extracted.get(0).scopeRefId()).isEqualTo("course-1");
        assertThat(extracted.get(1).role()).isEqualTo("STUDENT");
        assertThat(extracted.get(1).scopeType()).isEqualTo("COURSE");
        assertThat(extracted.get(1).scopeRefId()).isEqualTo("course-2");
    }

    @Test
    void extractMemberships_shouldReturnEmptyListWhenNoMembershipsInToken() {
        String token = service.generate("user-ghi", List.of());

        List<MembershipClaim> extracted = service.extractMemberships(token);

        assertThat(extracted).isEmpty();
    }

    @Test
    void validate_shouldRejectExpiredToken() {
        JwtProperties props = new JwtProperties(
                "my-test-secret-key-that-is-long-enough-for-hmac-sha256!!",
                1L // 1 millisecond
        );
        JwtService shortService = new JwtService(props);
        String token = shortService.generate("user-jkl", List.of());

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean valid = shortService.validate(token);

        assertThat(valid).isFalse();
    }
}
