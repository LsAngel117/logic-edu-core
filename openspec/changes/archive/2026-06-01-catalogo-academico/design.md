# Design: Catálogo Académico — Subject Domain

## Technical Approach

Exact mirror of AcademicLevel pattern: immutable aggregate, record Id, per-operation use cases, JPA adapter, nested REST controller under school. Subject replaces `number` with `code` and adds `description`/`hours`.

## Architecture Decisions

| Decision | Options | Choice | Rationale |
|----------|---------|--------|-----------|
| Subject as own aggregate root | (a) aggregate under School, (b) standalone aggregate | (b) | Consistent with AcademicLevel, Period, Structure — each is its own root |
| URL nesting | (a) `/api/v1/subjects`, (b) `/api/v1/schools/{schoolId}/subjects` | (b) | School is the parent scope; matches levels/periods pattern |
| Deactivate idempotency | (a) throw if already INACTIVE, (b) return 200 no-op | (b) | Spec requires idempotent. Domain `deactivate()` returns `this` if already INACTIVE; service does NOT throw |
| code uniqueness | (a) domain check, (b) application check | (b) | Requires repository query; matched to AcademicLevel `existsBySchoolIdAndNumber` |
| changeData as single method | (a) separate changeName/changeCode/changeDescription/changeHours, (b) single changeData(code,name,desc,hours) | (b) | Simpler API; matching spec's Update Subject requirement which sends all fields |

## Data Flow

```
Request → Controller (map→Command) → UseCase → Repository (domain) → Adapter → JPA
                  ↑                        ↓                                │
              Response (←Result)     @Transactional                   entity.save()
```

Code uniqueness flow on create/update: use case calls `existsBySchoolIdAndCode()` → throws `IllegalArgumentException` for conflict (caught as 409 in controller).

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/academic/subject/model/Subject.java` | Create | Immutable aggregate: SubjectId, SchoolId, code, name, description, hours, status, createdAt, updatedAt. Factory: `create()`, `restore()`. Behaviors: `changeData()`, `deactivate()`. Validate: hours >= 0 |
| `domain/academic/subject/model/SubjectId.java` | Create | Record(String value) with `generate()` |
| `domain/academic/subject/model/SubjectStatus.java` | Create | Enum ACTIVE/INACTIVE |
| `application/academic/subject/port/in/CreateSubjectUseCase.java` | Create | Interface: `SubjectResult execute(CreateSubjectCommand)` |
| `application/academic/subject/port/in/GetSubjectUseCase.java` | Create | Interface: `SubjectResult execute(SubjectId)` |
| `application/academic/subject/port/in/ListSubjectsBySchoolUseCase.java` | Create | Interface: `List<SubjectResult> execute(SchoolId)` |
| `application/academic/subject/port/in/UpdateSubjectUseCase.java` | Create | Interface: `SubjectResult execute(UpdateSubjectCommand)` |
| `application/academic/subject/port/in/DeactivateSubjectUseCase.java` | Create | Interface: `SubjectResult execute(SubjectId)` |
| `application/academic/subject/port/out/SubjectRepository.java` | Create | Interface: save, findById, findBySchoolId, existsBySchoolIdAndCode |
| `application/academic/subject/dto/command/CreateSubjectCommand.java` | Create | Record: SubjectId, SchoolId, code, name, description, hours |
| `application/academic/subject/dto/command/UpdateSubjectCommand.java` | Create | Record: SubjectId, SchoolId, code, name, description, hours |
| `application/academic/subject/dto/result/SubjectResult.java` | Create | Record with static `from(Subject)` |
| `application/academic/subject/usecase/CreateSubjectService.java` | Create | Validate code uniqueness, create domain, save. @Transactional |
| `application/academic/subject/usecase/GetSubjectService.java` | Create | Find by id or throw "not found" |
| `application/academic/subject/usecase/ListSubjectsBySchoolService.java` | Create | Find all by school |
| `application/academic/subject/usecase/UpdateSubjectService.java` | Create | Validate ACTIVE, check code uniqueness if changed, call changeData(). @Transactional |
| `application/academic/subject/usecase/DeactivateSubjectService.java` | Create | Find, deactivate (idempotent), save. @Transactional |
| `infrastructure/.../subject/persistence/entity/SubjectEntity.java` | Create | JPA @Entity, table="subjects", plain getters/setters |
| `infrastructure/.../subject/persistence/repository/SubjectJpaRepository.java` | Create | JpaRepository<SubjectEntity,String>, findAllBySchoolId, existsBySchoolIdAndCode |
| `infrastructure/.../subject/persistence/adapter/SubjectRepositoryAdapter.java` | Create | Implements SubjectRepository, maps entity↔domain |
| `interfaces/rest/.../subject/controller/SubjectController.java` | Create | @RequestMapping("/api/v1/schools/{schoolId}/subjects"), 5 endpoints |
| `interfaces/rest/.../subject/dto/request/CreateSubjectRequest.java` | Create | Record: code, name, description, hours |
| `interfaces/rest/.../subject/dto/request/UpdateSubjectRequest.java` | Create | Record: code, name, description, hours |
| `interfaces/rest/.../subject/dto/response/SubjectResponse.java` | Create | Record with static `from(SubjectResult)` |
| `src/main/resources/db/migration/V9__create_subjects_table.sql` | Create | FK to schools, UNIQUE(school_id, code), columns: id, school_id, code, name, description, hours, status, created_at, updated_at |
| `infrastructure/config/AcademicBeansConfig.java` | Modify | Add 5 @Bean methods for Subject use cases |

## Interfaces / Contracts

**Subject domain:**

```java
// Factory: Subject.create(SubjectId, SchoolId, code, name, description, hours, now)
// Factory: Subject.restore(SubjectId, SchoolId, code, name, description, hours, status, createdAt, updatedAt)
// Behavior: Subject.changeData(code, name, description, hours, now) → new Subject
// Behavior: Subject.deactivate(now) → new Subject (or this if already INACTIVE)
// Invariant: hours >= 0
```

**Controller endpoints:**

| Method | Path | Auth | Status |
|--------|------|------|--------|
| POST | `/api/v1/schools/{schoolId}/subjects` | SCHOOL_ADMIN/PLATFORM_ADMIN | 201/409/404/422/401/403 |
| GET | `/api/v1/schools/{schoolId}/subjects/{id}` | isAuthenticated | 200/404/401 |
| GET | `/api/v1/schools/{schoolId}/subjects` | isAuthenticated | 200/401 |
| PUT | `/api/v1/schools/{schoolId}/subjects/{id}` | SCHOOL_ADMIN/PLATFORM_ADMIN | 200/409/422/404/401/403 |
| PATCH | `/api/v1/schools/{schoolId}/subjects/{id}/deactivate` | SCHOOL_ADMIN/PLATFORM_ADMIN | 200/401/403/404 |

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit — Domain | Subject invariant (hours>=0), create, restore, changeData, deactivate idempotency | JUnit 5, pure unit |
| Unit — Application | Service logic with mocked SubjectRepository, code uniqueness, status validation | JUnit 5 + Mockito |
| Integration | JPA: unique constraint on (school_id,code), CRUD via adapter. REST: full endpoint status codes per spec | Spring Boot test slices |
| Unit — Adapter | mapToEntity / mapToDomain round-trip | JUnit 5 |

## Migration

V9__create_subjects_table.sql — new table only, no data migration required.

## Open Questions

- None
