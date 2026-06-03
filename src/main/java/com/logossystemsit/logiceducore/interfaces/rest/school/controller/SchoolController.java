package com.logossystemsit.logiceducore.interfaces.rest.school.controller;

import com.logossystemsit.logiceducore.application.school.dto.command.CreateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import com.logossystemsit.logiceducore.interfaces.rest.dto.response.SchoolResponse;
import com.logossystemsit.logiceducore.interfaces.rest.school.dto.request.CreateSchoolRequest;
import com.logossystemsit.logiceducore.interfaces.rest.school.dto.request.UpdateSchoolRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@Tag(name = "Instituciones", description = "Gestión de instituciones educativas")
public class SchoolController {

    private final CreateSchoolUseCase createSchoolUseCase;
    private final GetSchoolUseCase getSchoolUseCase;
    private final ListSchoolsUseCase listSchoolsUseCase;
    private final UpdateSchoolUseCase updateSchoolUseCase;
    private final DeactivateSchoolUseCase deactivateSchoolUseCase;

    public SchoolController(CreateSchoolUseCase createSchoolUseCase,
                            GetSchoolUseCase getSchoolUseCase,
                            ListSchoolsUseCase listSchoolsUseCase,
                            UpdateSchoolUseCase updateSchoolUseCase,
                            DeactivateSchoolUseCase deactivateSchoolUseCase) {
        this.createSchoolUseCase = createSchoolUseCase;
        this.getSchoolUseCase = getSchoolUseCase;
        this.listSchoolsUseCase = listSchoolsUseCase;
        this.updateSchoolUseCase = updateSchoolUseCase;
        this.deactivateSchoolUseCase = deactivateSchoolUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear institución", description = "Registra una nueva institución educativa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SchoolResponse> createSchool(@RequestBody CreateSchoolRequest request) {
        try {
            CreateSchoolCommand command = mapToCreateCommand(request);
            SchoolResult result = createSchoolUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener institución", description = "Consulta una institución por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SchoolResponse> getSchool(@PathVariable String id) {
        try {
            SchoolId schoolId = new SchoolId(id);
            SchoolResult result = getSchoolUseCase.execute(schoolId);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar instituciones", description = "Obtiene todas las instituciones registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SchoolResponse>> listSchools() {
        List<SchoolResult> results = listSchoolsUseCase.execute();
        List<SchoolResponse> responses = results.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar institución", description = "Modifica los datos de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SchoolResponse> updateSchool(@PathVariable String id,
                                                        @RequestBody UpdateSchoolRequest request) {
        try {
            SchoolId schoolId = new SchoolId(id);
            UpdateSchoolCommand command = mapToUpdateCommand(schoolId, request);
            SchoolResult result = updateSchoolUseCase.execute(command);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar institución", description = "Desactiva una institución educativa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SchoolResponse> deactivateSchool(@PathVariable String id) {
        try {
            SchoolId schoolId = new SchoolId(id);
            SchoolResult result = deactivateSchoolUseCase.execute(schoolId);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateSchoolCommand mapToCreateCommand(CreateSchoolRequest r) {
        SchoolId schoolId = SchoolId.generate();
        SchoolName name = new SchoolName(r.name());
        SchoolCode code = new SchoolCode(r.code());
        SchoolShortName shortName = SchoolShortName.of(r.shortName());
        SchoolDescription description = r.description() != null && !r.description().isBlank()
                ? SchoolDescription.of(r.description())
                : SchoolDescription.empty();
        SchoolEmail email = r.email() != null && !r.email().isBlank()
                ? SchoolEmail.of(r.email())
                : null;
        SchoolPhone phone = r.phone() != null && !r.phone().isBlank()
                ? SchoolPhone.of(r.phone())
                : null;
        SchoolAddress address = r.address() != null && !r.address().isBlank()
                ? SchoolAddress.of(r.address())
                : SchoolAddress.empty();

        return new CreateSchoolCommand(schoolId, name, code, shortName, description, email, phone, address);
    }

    private UpdateSchoolCommand mapToUpdateCommand(SchoolId schoolId, UpdateSchoolRequest r) {
        SchoolName name = new SchoolName(r.name());
        SchoolCode code = new SchoolCode(r.code());
        SchoolShortName shortName = SchoolShortName.of(r.shortName());
        SchoolDescription description = r.description() != null && !r.description().isBlank()
                ? SchoolDescription.of(r.description())
                : SchoolDescription.empty();
        SchoolEmail email = r.email() != null && !r.email().isBlank()
                ? SchoolEmail.of(r.email())
                : null;
        SchoolPhone phone = r.phone() != null && !r.phone().isBlank()
                ? SchoolPhone.of(r.phone())
                : null;
        SchoolAddress address = r.address() != null && !r.address().isBlank()
                ? SchoolAddress.of(r.address())
                : SchoolAddress.empty();

        return new UpdateSchoolCommand(schoolId, name, code, shortName, description, email, phone, address);
    }

    private SchoolResponse toResponse(SchoolResult result) {
        return new SchoolResponse(
                result.id(),
                result.name(),
                result.code(),
                result.shortName(),
                result.description(),
                result.email(),
                result.phone(),
                result.address(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
