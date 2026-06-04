package com.logossystemsit.logiceducore.interfaces.rest.advice;

import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.ErrorResponse;
import com.logossystemsit.logiceducore.shared.errors.exceptions.AuthenticationException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/v1/users");
    }

    @Test
    void handleAuthenticationException_shouldReturn401() {
        var ex = new AuthenticationException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Invalid credentials");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.AUTH_INVALID_CREDENTIALS);
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().path()).isEqualTo("/api/v1/users");
    }

    @Test
    void handleResourceNotFound_shouldReturn404() {
        var ex = new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User not found");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("User not found");
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().path()).isEqualTo("/api/v1/users");
    }

    @Test
    void handleBusinessRule_shouldReturn422() {
        var ex = new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "CC requires legal age");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.VALIDATION_ERROR);
        assertThat(response.getBody().message()).isEqualTo("CC requires legal age");
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().path()).isEqualTo("/api/v1/users");
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        var ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.AUTH_ACCESS_DENIED);
        assertThat(response.getBody().message()).isEqualTo("Access denied");
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    void handleGenericException_shouldReturn500() {
        var ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("Unexpected server error");
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}
