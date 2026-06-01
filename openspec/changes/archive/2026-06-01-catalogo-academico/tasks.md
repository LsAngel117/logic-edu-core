# Tasks: Catálogo Académico — Subject Domain

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~1200 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1: Foundation+Application (~600 lines) → PR 2: REST+Tests (~600 lines) |
| Delivery strategy | ask-always |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: pending
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | V9 + Domain model + Ports + DTOs + Use Cases + Domain/Service tests | PR 1 | Core logic, no REST dependency. Base: main |
| 2 | REST controller + Request/Response DTOs + Beans config + Controller/JPA/Adapter tests | PR 2 | Builds on PR 1. Base: main (stacked) or PR#1 branch (chained) |

## Phase 1: Foundation — Infrastructure + Domain

- [x] 1.1 Create `V9__create_subjects_table.sql` — FK to schools, UNIQUE(school_id,code)
- [x] 1.2 Create `SubjectId.java` record with `generate()` and blank validation
- [x] 1.3 Create `SubjectStatus.java` enum ACTIVE/INACTIVE
- [x] 1.4 Create `Subject.java`: immutable aggregate, create/restore/changeData/deactivate, hours>=0
- [x] 1.5 Create `SubjectEntity.java` JPA @Entity mapping to `subjects` table
- [x] 1.6 Create `SubjectJpaRepository.java` extending JpaRepository
- [x] 1.7 Create `SubjectRepository.java` port: save, findById, findBySchoolId, existsBySchoolIdAndCode
- [x] 1.8 Create `SubjectRepositoryAdapter.java` entity↔domain mapping

## Phase 2: Application — Use Cases

- [x] 2.1 Create `CreateSubjectCommand.java` and `UpdateSubjectCommand.java` records
- [x] 2.2 Create `SubjectResult.java` record with static `from(Subject)`
- [x] 2.3 Create 5 port-in interfaces: Create/Get/ListBySchool/Update/DeactivateSubjectUseCase
- [x] 2.4 Create `CreateSubjectService.java` — validate uniqueness, create, save
- [x] 2.5 Create `GetSubjectService.java` — find by id or throw
- [x] 2.6 Create `ListSubjectsBySchoolService.java` — find all by school
- [x] 2.7 Create `UpdateSubjectService.java` — validate ACTIVE, check code if changed, changeData
- [x] 2.8 Create `DeactivateSubjectService.java` — find, deactivate, save (idempotent)

## Phase 3: REST + Wiring

- [x] 3.1 Create `CreateSubjectRequest.java` and `UpdateSubjectRequest.java`
- [x] 3.2 Create `SubjectResponse.java` with `from(SubjectResult)`
- [x] 3.3 Create `SubjectController.java` — 5 endpoints at `/api/v1/schools/{schoolId}/subjects`
- [x] 3.4 Add 5 `@Bean` methods to `AcademicBeansConfig.java` for Subject use cases

## Phase 4: Tests

- [x] 4.1 Write `SubjectTest.java`: hours>=0, create/restore/changeData/deactivate, immutability
- [x] 4.2 Write 5 service tests: Create/Get/ListBySchool/Update/Deactivate with mocked repo
- [x] 4.3 Write `SubjectControllerTest.java`: status codes per spec (201/200/404/409/422/401/403)
- [x] 4.4 Write `SubjectJpaRepositoryTest.java`: unique constraint, CRUD
- [x] 4.5 Write `SubjectRepositoryAdapterTest.java`: mapToEntity/mapToDomain round-trip
