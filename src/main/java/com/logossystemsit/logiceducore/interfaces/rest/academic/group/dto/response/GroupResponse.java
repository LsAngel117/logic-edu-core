package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.response;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GroupResponse(
        @Schema(description = "Identificador único del grupo", example = "01JT6K1N2O3P4Q5R6S7T8U9V")
        String id,
        @Schema(description = "Identificador de la institución", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String schoolId,
        @Schema(description = "Identificador de la materia", example = "01JT6J0M1N2O3P4Q5R6S7T8U")
        String subjectId,
        @Schema(description = "Identificador del período académico", example = "01JT6H8K9L0M1N2O3P4Q5R6S")
        String academicPeriodId,
        @Schema(description = "Identificador de la sede", example = "01JT5E5H6I7J8K9L0M1N2O3P")
        String branchId,
        @Schema(description = "Identificador del docente", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String teacherId,
        @Schema(description = "Código único del grupo", example = "GRP-MAT-001")
        String code,
        @Schema(description = "Capacidad máxima de estudiantes", example = "30")
        int capacity,
        @Schema(description = "Estado del grupo: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Versión para control de concurrencia", example = "1")
        Long version,
        @Schema(description = "Lista de horarios del grupo")
        List<ScheduleResponse> schedules,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {
    public static GroupResponse from(GroupResult result) {
        List<ScheduleResponse> scheduleResponses = result.schedules().stream()
                .map(s -> new ScheduleResponse(
                        s.id(), s.dayOfWeek(),
                        s.startTime() != null ? s.startTime().toString() : null,
                        s.endTime() != null ? s.endTime().toString() : null,
                        s.classroom()))
                .toList();

        return new GroupResponse(
                result.id(),
                result.schoolId(),
                result.subjectId(),
                result.academicPeriodId(),
                result.branchId(),
                result.teacherId(),
                result.code(),
                result.capacity(),
                result.status(),
                result.version(),
                scheduleResponses,
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }

    public record ScheduleResponse(
            @Schema(description = "Identificador del horario", example = "01JT6K2O3P4Q5R6S7T8U9V0W")
            String id,
            @Schema(description = "Día de la semana", example = "MONDAY")
            String dayOfWeek,
            @Schema(description = "Hora de inicio", example = "08:00")
            String startTime,
            @Schema(description = "Hora de fin", example = "10:00")
            String endTime,
            @Schema(description = "Aula o salón", example = "A-101")
            String classroom
    ) {}
}
