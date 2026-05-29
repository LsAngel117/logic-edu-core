package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.controller;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.CreateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.UpdateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request.CreateEvaluationPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request.UpdateEvaluationPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.response.EvaluationPeriodResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/periods/{periodId}/evaluations")
public class EvaluationPeriodController {

    private final CreateEvaluationPeriodUseCase createUseCase;
    private final GetEvaluationPeriodUseCase getUseCase;
    private final ListEvaluationPeriodsByPeriodUseCase listUseCase;
    private final UpdateEvaluationPeriodUseCase updateUseCase;
    private final DeactivateEvaluationPeriodUseCase deactivateUseCase;

    public EvaluationPeriodController(
            CreateEvaluationPeriodUseCase createUseCase,
            GetEvaluationPeriodUseCase getUseCase,
            ListEvaluationPeriodsByPeriodUseCase listUseCase,
            UpdateEvaluationPeriodUseCase updateUseCase,
            DeactivateEvaluationPeriodUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<EvaluationPeriodResponse> create(
            @PathVariable String periodId,
            @RequestBody CreateEvaluationPeriodRequest request) {
        try {
            CreateEvaluationPeriodCommand command = mapToCreateCommand(periodId, request);
            EvaluationPeriodResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EvaluationPeriodResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EvaluationPeriodResponse> getById(
            @PathVariable String periodId,
            @PathVariable String id) {
        try {
            EvaluationPeriodId evalId = new EvaluationPeriodId(id);
            EvaluationPeriodResult result = getUseCase.execute(evalId);
            return ResponseEntity.ok(EvaluationPeriodResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvaluationPeriodResponse>> listByPeriod(
            @PathVariable String periodId) {
        AcademicPeriodId pid = new AcademicPeriodId(periodId);
        List<EvaluationPeriodResult> results = listUseCase.execute(pid);
        List<EvaluationPeriodResponse> responses = results.stream()
                .map(EvaluationPeriodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<EvaluationPeriodResponse> update(
            @PathVariable String periodId,
            @PathVariable String id,
            @RequestBody UpdateEvaluationPeriodRequest request) {
        try {
            UpdateEvaluationPeriodCommand command = mapToUpdateCommand(periodId, id, request);
            EvaluationPeriodResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(EvaluationPeriodResponse.from(result));
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
    public ResponseEntity<EvaluationPeriodResponse> deactivate(
            @PathVariable String periodId,
            @PathVariable String id) {
        try {
            EvaluationPeriodId evalId = new EvaluationPeriodId(id);
            EvaluationPeriodResult result = deactivateUseCase.execute(evalId);
            return ResponseEntity.ok(EvaluationPeriodResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateEvaluationPeriodCommand mapToCreateCommand(String periodId,
                                                              CreateEvaluationPeriodRequest r) {
        return new CreateEvaluationPeriodCommand(
                EvaluationPeriodId.generate(),
                new AcademicPeriodId(periodId),
                r.name(),
                r.sequence(),
                new BigDecimal(r.weight()),
                r.startDate() != null ? LocalDate.parse(r.startDate()) : null,
                r.endDate() != null ? LocalDate.parse(r.endDate()) : null
        );
    }

    private UpdateEvaluationPeriodCommand mapToUpdateCommand(String periodId, String id,
                                                              UpdateEvaluationPeriodRequest r) {
        return new UpdateEvaluationPeriodCommand(
                new EvaluationPeriodId(id),
                new AcademicPeriodId(periodId),
                r.name(),
                r.sequence() != null ? Integer.parseInt(r.sequence()) : null,
                r.weight() != null ? new BigDecimal(r.weight()) : null,
                r.startDate() != null ? LocalDate.parse(r.startDate()) : null,
                r.endDate() != null ? LocalDate.parse(r.endDate()) : null
        );
    }
}
