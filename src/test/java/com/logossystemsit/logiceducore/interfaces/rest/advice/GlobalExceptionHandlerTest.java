package com.logossystemsit.logiceducore.interfaces.rest.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleIllegalArgument_shouldReturn422() {
        var ex = new IllegalArgumentException("CC requires legal age");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("CC requires legal age");
        assertThat(response.getBody().get("status")).isEqualTo(422);
    }

    @Test
    void handleIllegalState_shouldReturn403() {
        var ex = new IllegalStateException("User already blocked");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("User already blocked");
        assertThat(response.getBody().get("status")).isEqualTo(403);
    }

    @Test
    void handleNoSuchElement_shouldReturn404() {
        var ex = new NoSuchElementException("User not found");

        ResponseEntity<Map<String, Object>> response = handler.handleNoSuchElement(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("User not found");
        assertThat(response.getBody().get("status")).isEqualTo(404);
    }

    @Test
    void handleGenericException_shouldReturn500() {
        var ex = new RuntimeException("Unexpected error");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Unexpected error");
        assertThat(response.getBody().get("status")).isEqualTo(500);
    }

    @Test
    void handleGenericException_shouldReturnInternalErrorMessageWhenMessageIsNull() {
        var ex = new RuntimeException();

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Internal server error");
        assertThat(response.getBody().get("status")).isEqualTo(500);
    }

    @Test
    void handleResponseStatus_shouldReturnOriginalStatusCode() {
        var ex = new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");

        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Email already in use");
        assertThat(response.getBody().get("status")).isEqualTo(409);
    }
}
