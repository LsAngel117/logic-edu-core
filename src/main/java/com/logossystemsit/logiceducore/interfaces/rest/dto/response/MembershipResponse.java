package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MembershipResponse(
        @Schema(description = "Identificador único de la membresía", example = "01JT5C3F4G5H6I7J8K9L0M1N")
        String id,
        @Schema(description = "Identificador del usuario al que pertenece la membresía", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String userId,
        @Schema(description = "Rol del usuario", example = "TEACHER")
        String role,
        @Schema(description = "Tipo de alcance de la membresía", example = "SCHOOL")
        String scopeType,
        @Schema(description = "ID de referencia del alcance (puede ser nulo si el alcance es ALL)", example = "a1b2c3d4")
        String scopeRefId,
        @Schema(description = "Indica si la membresía está activa", example = "true")
        boolean active
) {
}
