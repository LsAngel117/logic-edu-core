package com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnrollStudentRequest(
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String userId,
        @Schema(description = "Identificador del grupo al que se inscribe", example = "01JT6K1N2O3P4Q5R6S7T8U9V")
        String groupId
) {}
