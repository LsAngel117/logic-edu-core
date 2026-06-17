package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequest(
        @Schema(description = "Nombre de usuario único en la plataforma", example = "juan.perez")
        String username,
        @Schema(description = "Correo electrónico del usuario", example = "usuario@institucion.edu.co")
        String email,
        @Schema(description = "Contraseña en texto plano", example = "MiPassword123")
        String rawPassword,
        @Schema(description = "Primer nombre", example = "Juan")
        String firstGivenName,
        @Schema(description = "Segundo nombre (opcional)", example = "Carlos")
        String secondGivenName,
        @Schema(description = "Primer apellido", example = "Pérez")
        String firstFamilyName,
        @Schema(description = "Segundo apellido (opcional)", example = "Gómez")
        String secondFamilyName,
        @Schema(description = "Sexo: MALE o FEMALE", example = "MALE")
        String sex,
        @Schema(description = "Fecha de nacimiento en formato YYYY-MM-DD", example = "2000-05-15")
        String birthDate,
        @Schema(description = "Tipo de documento: CC, TI, CE, etc.", example = "CC")
        String documentType,
        @Schema(description = "Número de documento", example = "1234567890")
        String documentValue,
        @Schema(description = "Teléfono (opcional)", example = "+57 300 123 4567")
        String phone,
        @Schema(description = "Dirección (opcional)", example = "Calle 123 #45-67")
        String address,
        @Schema(description = "Ciudad (opcional)", example = "Medellín")
        String city,
        @Schema(description = "País (opcional)", example = "Colombia")
        String country,
        @Schema(description = "Rol del usuario: PLATFORM_ADMIN, SCHOOL_ADMIN, TEACHER, STUDENT", example = "TEACHER")
        String role,
        @Schema(description = "Tipo de alcance: SCHOOL, BRANCH, ALL", example = "SCHOOL")
        String scopeType,
        @Schema(description = "ID de referencia del alcance (schoolId o branchId)", example = "a1b2c3d4")
        String scopeRefId
) {
}
