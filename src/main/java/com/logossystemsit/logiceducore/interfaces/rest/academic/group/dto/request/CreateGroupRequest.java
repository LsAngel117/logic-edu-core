package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateGroupRequest(
        @JsonProperty("subjectId")
        @Schema(description = "Identificador de la materia", example = "01JT6J0M1N2O3P4Q5R6S7T8U")
        String subjectId,
        @JsonProperty("academicPeriodId")
        @Schema(description = "Identificador del período académico", example = "01JT6H8K9L0M1N2O3P4Q5R6S")
        String academicPeriodId,
        @JsonProperty("branchId")
        @Schema(description = "Identificador de la sede", example = "01JT5E5H6I7J8K9L0M1N2O3P")
        String branchId,
        @JsonProperty("teacherId")
        @Schema(description = "Identificador del docente", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String teacherId,
        @JsonProperty("code")
        @Schema(description = "Código único del grupo", example = "GRP-MAT-001")
        String code,
        @JsonProperty("capacity")
        @Schema(description = "Capacidad máxima de estudiantes", example = "30")
        int capacity,
        @JsonProperty("schedules")
        @Schema(description = "Lista de horarios del grupo")
        List<ScheduleRequest> schedules
) {

    public record ScheduleRequest(
            @JsonProperty("dayOfWeek")
            @Schema(description = "Día de la semana: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY", example = "MONDAY")
            String dayOfWeek,
            @JsonProperty("startTime")
            @Schema(description = "Hora de inicio en formato HH:mm", example = "08:00")
            String startTime,
            @JsonProperty("endTime")
            @Schema(description = "Hora de fin en formato HH:mm", example = "10:00")
            String endTime,
            @JsonProperty("classroom")
            @Schema(description = "Aula o salón", example = "A-101")
            String classroom
    ) {}
}
