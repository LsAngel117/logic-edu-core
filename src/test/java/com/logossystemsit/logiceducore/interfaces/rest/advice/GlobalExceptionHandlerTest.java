package com.logossystemsit.logiceducore.interfaces.rest.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.NoSuchElementException;

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
    void handleIllegalArgument_shouldReturn422() {
        var ex = new IllegalArgumentException("CC requires legal age");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("CC requires legal age");
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().path()).isEqualTo("/api/v1/users");
    }

    @Test
    void handleIllegalState_shouldReturn409() {
        var ex = new IllegalStateException("User already blocked");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("User already blocked");
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    void handleNoSuchElement_shouldReturn404() {
        var ex = new NoSuchElementException("User not found");

        ResponseEntity<ErrorResponse> response = handler.handleNoSuchElement(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("User not found");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        var ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Sin permisos para ejecutar esta acción");
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    void handleGenericException_shouldReturn500() {
        var ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}
