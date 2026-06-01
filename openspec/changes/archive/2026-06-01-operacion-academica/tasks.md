# Tasks: Operación Académica — Group & Enrollment

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~3500–4500 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (Group: domain/JPA/app/REST/V10/tests) → PR 2 (Enrollment: domain/JPA/app/REST/V11/tests) |
| Delivery strategy | ask-on-risk |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: pending
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Base | Notes |
|------|------|-----------|------|-------|
| 1 | Group aggregate vertical slice | PR 1 | main | Self-contained: domain, JPA, 6 services, REST, V10, tests |
| 2 | Enrollment aggregate vertical slice | PR 2 | main | Depends on Group: cross-aggregate rules, V11, tests |

## Phase 1: Foundation — Domain + Flyway + JPA

- [x] 1.1 Create `V10__create_groups.sql` (groups + group_schedules) + `V11__create_enrollments.sql` — all migrations complete
- [x] 1.2 Create Group domain: `Group.java`, `GroupId.java`, `GroupStatus.java`, `Schedule.java`
- [x] 1.3 Create Group JPA: `GroupEntity.java`, `GroupScheduleEntity.java`, `GroupJpaRepository.java`, `GroupRepositoryAdapter.java`
- [x] 1.4 Create Enrollment domain: `Enrollment.java`, `EnrollmentId.java`, `EnrollmentStatus.java` + 23 domain tests
- [x] 1.5 Create Enrollment JPA: `EnrollmentEntity.java`, `EnrollmentJpaRepository.java` (with custom @Query), `EnrollmentRepositoryAdapter.java` + 15 infra tests

## Phase 2: Group Application Layer

- [x] 2.1 Create `GroupRepository.java` output port
- [x] 2.2 Create 6 input ports
- [x] 2.3 Create DTOs: `CreateGroupCommand`, `UpdateGroupCommand`, `GroupResult`
- [x] 2.4 Create `CreateGroupService` — 6-entity validation
- [x] 2.5 Create `UpdateGroupService`, `UpdateGroupSchedulesService`, `DeactivateGroupService`, `GetGroupService`, `ListGroupsService`

## Phase 3: Enrollment Application Layer

- [x] 3.1 Create `EnrollmentRepository.java` output port (save, findById, findByGroupId, countActiveByGroupId, existsByUserIdAndGroupId, existsActiveByStudentAndSubjectAndPeriod)
- [x] 3.2 Create 4 input ports: `EnrollStudentUseCase`, `GetEnrollmentUseCase`, `ListEnrollmentsByGroupUseCase`, `DropEnrollmentUseCase`
- [x] 3.3 Create DTOs: `EnrollStudentCommand`, `EnrollmentResult` with from(Enrollment) factory
- [x] 3.4 Create `EnrollStudentService` — 7-step flow: find group→validate student active→check duplicate→check subject/period conflict→check capacity→save enrollment→save group (optimistic lock) + 9 tests
- [x] 3.5 Create `DropEnrollmentService` (idempotent), `GetEnrollmentService`, `ListEnrollmentsByGroupService` + 7 tests

## Phase 4: REST + Configuration

- [x] 4.1 Create `GroupController` + request/response DTOs
- [x] 4.2 Create `EnrollmentController` + request/response DTOs + 12 controller tests
- [x] 4.3 Add Group + Enrollment service beans to `AcademicBeansConfig`; add Group + Enrollment adapters to `PersistenceConfig`

## Phase 5: Tests

- [x] 5.1 Write Group domain tests: 34 cases (Group.create, Schedule, deactivate, changeData, changeSchedules)
- [x] 5.2 Write Group application tests: CreateGroupService (13 cases) + GroupServicesTest (13 cases) = 26 total
- [x] 5.3 Write Group infrastructure tests: GroupRepositoryAdapterTest (11 cases) + GroupJpaRepositoryTest (8 cases) = 19 total
- [x] 5.4 Write GroupControllerTest: 16 standalone MockMvc cases
- [x] 5.5 Write Enrollment tests: Domain 23 + JPA 6 + Adapter 9 + EnrollStudentService 9 + Other services 7 + Controller 12 = 66 cases
