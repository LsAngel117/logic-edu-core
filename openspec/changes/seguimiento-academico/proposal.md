# Proposal: Seguimiento Académico — Attendance, Assessment, Grade

## Intent

Implement the final academic phase: attendance tracking, assessment definition, and student grade recording. Enables teachers to take attendance per group/date, create and manage assessments, and register individual grades with teacher-ownership validation.

## Scope

### In Scope
- Attendance aggregate root, 4 use cases: Register, Get, ListByGroup, Update
- Assessment aggregate root (optional FK to EvaluationPeriod), 5 use cases: Create, Get, ListByGroup, Update, Delete
- Grade aggregate root, 4 use cases: Register, Get, ListByAssessment, Update
- Cross-aggregate auth: Grade R/W chains Grade → Assessment → Group → teacherId
- Flyway V12 (attendances), V13 (assessments), V14 (grades)
- Hexagonal layering: Domain → Application → Infrastructure → Interfaces

### Out of Scope
- Bulk grade operations (upsertAll) — deferred
- Final grade computation / weighted averages — deferred
- Notifications on grade publication — deferred

## Capabilities

### New Capabilities
- `attendance`: Register and manage student attendance per group/date with status PRESENT/ABSENT/LATE/EXCUSED, UK (group_id, date, student_id)
- `assessment`: Create and manage assessments per group, optionally linked to EvaluationPeriod, UK (group_id, name)
- `grade`: Register and update individual student grades per assessment with cross-aggregate teacher validation, UK (assessment_id, student_id)

### Modified Capabilities
- None

## Approach

Follow existing hexagonal patterns: `final` domain model with private ctor, `create`/`restore` factories, behavior methods returning new instances. Each aggregate gets its own package (model, spi, application service), JPA adapter, and REST controller. Cross-aggregate teacher ownership validated at Application Service layer via port query. No optimistic locking needed — grades are teacher-driven, low contention.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/academic/attendance/` | New | Attendance aggregate, AttendanceId, AttendanceStatus enum |
| `domain/academic/assessment/` | New | Assessment aggregate, AssessmentId, AssessmentType enum |
| `domain/academic/grade/` | New | Grade aggregate, GradeId |
| `application/` | New | AttendanceService, AssessmentService, GradeService |
| `infrastructure/jpa/` | New | 3 JPA adapters (entity + repository + mapper) |
| `interfaces/` | New | 3 REST controllers under `/api/v1` |
| `config/` | Modified | BeansConfig + PersistenceConfig for 3 new aggregates |
| `resources/db/migration/` | New | V12, V13, V14 SQL |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Cross-aggregate auth chain fragile | Medium | Single query `GroupRepository.findTeacherIdByAssessmentId()` |
| Scope creep (bulk ops, final grades) | Medium | Explicit out-of-scope, deferred to future |
| Assessment delete with existing grades | Low | Check GradeRepository before delete, return 409 |

## Rollback Plan

Drop Flyway V12–V14. Remove `attendance/`, `assessment/`, `grade/` packages across all layers. Restore config files.

## Dependencies

- Group aggregate (completed in Phase 3)
- EvaluationPeriod aggregate (completed)
- STUDENT and TEACHER roles seeded (Phase 1)

## Success Criteria

- [ ] All 13 use cases implemented with unit + integration tests passing
- [ ] Cross-aggregate auth: non-owner teacher receives 403 on Grade register
- [ ] Assessment delete with existing grades returns 409
- [ ] Flyway V12–V14 apply cleanly on fresh database
