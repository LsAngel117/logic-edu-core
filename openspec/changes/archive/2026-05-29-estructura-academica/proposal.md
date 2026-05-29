# Proposal: Estructura Académica

## Intent

Add core academic configuration: school academic models (structures), training levels, time periods, and evaluation sub-periods. Enables schools to define their academic year setup.

## Scope

### In Scope
- Flyway V5-V8 migrations (academic_structures, _levels, _periods, _evaluation_periods)
- 4 domain aggregates: AcademicStructure, AcademicLevel, AcademicPeriod, EvaluationPeriod (immutable, factories, SchoolId)
- Application CRUD services + period overlap validation + structure versioning
- JPA adapters + repositories
- REST controllers with @PreAuthorize(SCHOOL_ADMIN)
- Tests at unit, integration, and web-slice layers

### Out of Scope
- Subject, Group, Enrollment, Schedule, Attendance, Assessment, Grade
- Bulk import/migration of academic data

## Capabilities

### New Capabilities
- `academic-structure`: School academic model — levels count, periods config, versioning
- `academic-level`: Training levels within a structure — name, sequence
- `academic-period`: Time blocks within a level — dates, overlap validation
- `evaluation-period`: Optional sub-periods within a period — weight, dates

### Modified Capabilities
None — all new.

## Approach

Follow existing hexagonal pattern: Domain (immutable aggregate + factory) → Application (port interface + service) → Infra (JPA + adapter) → Web (controller). Period overlap via repository date-range query. Structure versioning via copy-from-previous factory. SCHOOL_ADMIN auth on all mutations.

## Affected Areas

| Area | Impact |
|------|--------|
| domain/academic/{structure,level,period,evaluation}/ | New |
| application/academic/ | New |
| infrastructure/academic/ | New |
| interfaces/rest/academic/ | New |
| db/migration/V5__..V8__..sql | New |
| test/**/academic/ | New |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Versioning complexity (new year = new version) | Low | Immutable model; factory copy |
| Overlap validation perf | Low | Index on (level_id, start_date, end_date) |
| Scope creep adding subjects/grades early | Medium | Strict out-of-scope enforcement |

## Rollback

Revert V5-V8 Flyway migrations, delete academic/ packages, restore git. If deployed: write down-migrations before releasing or roll back artifact.

## Dependencies

School aggregate (done), JWT security (done), school-management spec (no changes).

## Success Criteria

- [ ] All 4 CRUD endpoints return correct statuses (201/200/404/422)
- [ ] Overlapping periods return 422 with clear error message
- [ ] New structure version is independent copy (no mutation of previous)
- [ ] SCHOOL_ADMIN allowed on all mutations; other roles get 403
- [ ] All tests pass (unit + integration + web slice)
