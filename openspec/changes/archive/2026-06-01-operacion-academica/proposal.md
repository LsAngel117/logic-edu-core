# Proposal: Operación Académica — Group & Enrollment

## Intent

Implement Group management (with Schedule VO) and Enrollment — Phase 3 of the Academic Domain. Enables course group creation, scheduling, and concurrency-safe student enrollment with capacity control.

## Scope

### In Scope
- Group aggregate with 6 use cases: Create, Get, ListBySchool (optional branchId/periodId), Update, UpdateSchedules, Deactivate
- Schedule as VO of Group, persisted in `group_schedules` table, no independent lifecycle
- Enrollment aggregate with 4 use cases: Enroll, Get, ListByGroup, Drop
- Concurrency: optimistic locking via `Group.version` + unique constraint `(user_id, group_id)` as safety net
- Cross-aggregate rule: student cannot enroll in two groups of same subject+period
- Flyway V10 (groups + group_schedules), V11 (enrollments)
- Hexagonal layering: Domain → Application → Infrastructure → Interfaces

### Out of Scope
- Attendance, assessment, grades (Phase 4)
- Teacher schedule overlap validation

## Capabilities

### New Capabilities
- `group-management`: Group CRUD, schedule management, listing with optional filters, deactivation policy (preserves enrollments, rejects new ones)
- `enrollment-management`: Enroll/drop students, capacity control, cross-aggregate conflict validation, concurrency-safe

### Modified Capabilities
- None

## Approach

Follow existing Subject pattern: final domain models with `create`/`restore` static factories, Application Service + Port + DTO, JPA adapter with separate entity, REST controller. Group aggregate owns Schedule VOs in a dedicated table. Enrollment validates cross-aggregate rules at application layer via port queries. Deactivation is soft: `status=INACTIVE`, existing enrollments preserved, new enrollments return 422.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/model/group/` | New | Group aggregate, GroupId, GroupStatus, Schedule VO |
| `domain/model/enrollment/` | New | Enrollment aggregate, EnrollmentId, EnrollmentStatus |
| `domain/spi/GroupRepository` | New | Port: group + schedule persistence, capacity queries |
| `domain/spi/EnrollmentRepository` | New | Port: enrollment persistence |
| `application/GroupService` | New | 6 use cases |
| `application/EnrollmentService` | New | 4 use cases |
| `infrastructure/jpa/group/` | New | GroupEntity, ScheduleEntity, JPA repository |
| `infrastructure/jpa/enrollment/` | New | EnrollmentEntity, JPA repository |
| `interfaces/GroupController` | New | REST: `/api/v1/schools/{schoolId}/groups` |
| `interfaces/EnrollmentController` | New | REST: enrollments under groups |
| `config/` | Modified | AcademicBeansConfig + PersistenceConfig beans |
| `resources/db/migration/` | New | V10__groups.sql, V11__enrollments.sql |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Capacity race condition on enroll | Medium | Group.version optimistic lock + unique constraint |
| Cross-aggregate rule correctness | Medium | Query method on GroupRepository, validated in service |
| Scope creep | Low | Explicit out-of-scope, deferred to Phase 4 |

## Rollback Plan

Drop Flyway V10 and V11 migrations. Remove all Group and Enrollment packages across layers. Restore config files to previous state.

## Dependencies

- Subject, AcademicPeriod, Branch, User aggregates must exist (completed in earlier phases)
- TEACHER and STUDENT roles must be seeded (Phase 1)

## Success Criteria

- [ ] All 10 use cases implemented with unit + integration tests passing
- [ ] Concurrency test proves optimistic lock prevents over-capacity enrollment (2 concurrent requests, one fails)
- [ ] Cross-aggregate rule enforced: 422 on duplicate subject+period enrollment
- [ ] Deactivate preserves historical enrollments, rejects new ones with 422
- [ ] Flyway V10 and V11 apply cleanly on fresh database
