package com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateBranchRequest(
        @Schema(description = "Nombre de la sede", example = "Sede Principal Norte")
        String name,
        @Schema(description = "Código único de la sede", example = "SED-001")
        String code,
        @Schema(description = "Nombre corto de la sede", example = "NORTE")
        String shortName,
        @Schema(description = "Descripción de la sede (opcional)", example = "Sede principal ubicada en la zona norte")
        String description,
        @Schema(description = "Correo electrónico de la sede (opcional)", example = "sede.norte@sanjose.edu.co")
        String email,
        @Schema(description = "Teléfono de la sede (opcional)", example = "+57 601 3456789")
        String phone,
        @Schema(description = "Tipo de sede", example = "MAIN", allowableValues = {"MAIN", "SECONDARY", "VIRTUAL", "TEMPORARY"})
        String type,
        @Schema(description = "Dirección física de la sede (opcional)", example = "Calle 123 #45-67")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country
) {}
