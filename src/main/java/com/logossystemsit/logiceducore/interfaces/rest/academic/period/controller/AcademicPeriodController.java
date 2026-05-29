package com.logossystemsit.logiceducore.interfaces.rest.academic.period.controller;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.CreateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.command.UpdateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.PeriodType;
import com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request.CreateAcademicPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request.UpdateAcademicPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.response.AcademicPeriodResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/levels/{levelId}/periods")
public class AcademicPeriodController {

    private final CreateAcademicPeriodUseCase createUseCase;
    private final GetAcademicPeriodUseCase getUseCase;
    private final ListAcademicPeriodsByLevelUseCase listUseCase;
    private final UpdateAcademicPeriodUseCase updateUseCase;
    private final DeactivateAcademicPeriodUseCase deactivateUseCase;

    public AcademicPeriodController(
            CreateAcademicPeriodUseCase createUseCase,
            GetAcademicPeriodUseCase getUseCase,
            ListAcademicPeriodsByLevelUseCase listUseCase,
            UpdateAcademicPeriodUseCase updateUseCase,
            DeactivateAcademicPeriodUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<AcademicPeriodResponse> create(
            @PathVariable String levelId,
            @RequestBody CreateAcademicPeriodRequest request) {
        try {
            CreateAcademicPeriodCommand command = mapToCreateCommand(levelId, request);
            AcademicPeriodResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AcademicPeriodResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AcademicPeriodResponse> getById(@PathVariable String id) {
        try {
            AcademicPeriodId periodId = new AcademicPeriodId(id);
            AcademicPeriodResult result = getUseCase.execute(periodId);
            return ResponseEntity.ok(AcademicPeriodResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AcademicPeriodResponse>> listByLevel(@PathVariable String levelId) {
        AcademicLevelId lid = new AcademicLevelId(levelId);
        List<AcademicPeriodResult> results = listUseCase.execute(lid);
        List<AcademicPeriodResponse> responses = results.stream()
                .map(AcademicPeriodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<AcademicPeriodResponse> update(
            @PathVariable String levelId,
            @PathVariable String id,
            @RequestBody UpdateAcademicPeriodRequest request) {
        try {
            UpdateAcademicPeriodCommand command = mapToUpdateCommand(levelId, id, request);
            AcademicPeriodResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(AcademicPeriodResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<AcademicPeriodResponse> deactivate(@PathVariable String id) {
        try {
            AcademicPeriodId periodId = new AcademicPeriodId(id);
            AcademicPeriodResult result = deactivateUseCase.execute(periodId);
            return ResponseEntity.ok(AcademicPeriodResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateAcademicPeriodCommand mapToCreateCommand(String levelId, CreateAcademicPeriodRequest r) {
        return new CreateAcademicPeriodCommand(
                AcademicPeriodId.generate(),
                new AcademicLevelId(levelId),
                PeriodType.valueOf(r.periodType()),
                r.name(),
                r.sequence(),
                LocalDate.parse(r.startDate()),
                LocalDate.parse(r.endDate())
        );
    }

    private UpdateAcademicPeriodCommand mapToUpdateCommand(String levelId, String id,
                                                            UpdateAcademicPeriodRequest r) {
        return new UpdateAcademicPeriodCommand(
                new AcademicPeriodId(id),
                new AcademicLevelId(levelId),
                r.name(),
                r.sequence() != null ? Integer.parseInt(r.sequence()) : null,
                r.periodType() != null ? PeriodType.valueOf(r.periodType()) : null,
                r.startDate() != null ? LocalDate.parse(r.startDate()) : null,
                r.endDate() != null ? LocalDate.parse(r.endDate()) : null
        );
    }
}
