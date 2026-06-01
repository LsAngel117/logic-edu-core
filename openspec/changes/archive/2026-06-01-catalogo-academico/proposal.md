# Proposal: Catálogo Académico — Subject Domain

## Intent

Add Subject (materia/asignatura) to the academic catalog — Phase 2 of the 4-phase academic plan. Subject is a simple aggregate owned by a school, enabling course offering, enrollment, and grading in later phases.

## Scope

### In Scope
- Flyway V9 migration (`subjects` table)
- Domain aggregate: Subject (immutable, create/restore/changeData/deactivate)
- Application: 5 CRUD use cases (Create, Get, ListBySchool, Update, Deactivate)
- Infrastructure: JPA entity, repository, adapter
- REST: `/api/v1/schools/{schoolId}/subjects` (5 endpoints)
- Bean wiring in `AcademicBeansConfig`
- Tests at all layers (unit, integration, REST)

### Out of Scope
- Relation to academic periods / levels (deferred to Group in Phase 3)
- Bulk import/export
- Authorization beyond SCHOOL_ADMIN/isAuthenticated

## Capabilities

### New Capabilities
- `subject`: Subject catalog management — create, read, list, update, deactivate subjects scoped to a school.

### Modified Capabilities
- None

## Approach

Follow existing AcademicLevel pattern exactly: record Id, immutable domain with factory methods, status enum, use case per operation, JPA adapter, controller at `schools/{schoolId}/subjects`. Business rules enforced in domain (code unique per school, hours >= 0).

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/academic/subject/` | New | Subject aggregate, SubjectId, SubjectStatus |
| `application/academic/subject/` | New | 5 use cases, commands/results, ports |
| `infrastructure/.../subject/` | New | JPA entity, repo, adapter |
| `interfaces/rest/.../subject/` | New | REST controller, DTOs |
| `infrastructure/config/AcademicBeansConfig.java` | Modified | Wire 5 Subject use cases |
| `src/main/resources/db/migration/V9__*.sql` | New | Create subjects table |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Table naming collision with reserved words | Low | Use `subjects` (not `subject`) |
| Unique constraint violation exposed as 500 | Low | Catch in service with existsBySchoolIdAndCode check |

## Rollback Plan

- Drop V9 migration, remove all subject packages, revert AcademicBeansConfig.
- No downstream consumers yet (Phase 3 Group depends on Subject — safe).

## Dependencies

- School aggregate must exist (Phase 1 complete).
- AcademicBeansConfig pattern established.

## Success Criteria

- [ ] All 5 Subject use cases tested and passing (unit + integration)
- [ ] REST endpoints return correct status codes (201, 200, 404, 409, 422, 401, 403)
- [ ] Business rules enforced: code unique per school, hours >= 0, no update on INACTIVE
- [ ] V9 migration creates subjects table with unique index on (school_id, code)
