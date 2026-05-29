# Design: Estructura Académica

## Technical Approach

Follow existing hexagonal patterns (School/Branch as reference). All four concepts are independent aggregate roots with immutable domain objects, `create()`/`restore()` factories, copy-on-write behaviors, and string-record Value Objects. Application services enforce cross-aggregate rules at the service layer. URLs follow the spec-defined nested pattern. Flyway migrations V5–V8 create the schema with unique constraints and performance indexes.

## Architecture Decisions

### Decision 1: Aggregate Boundaries

| Option | Tradeoff | Decision |
|--------|----------|----------|
| EvaluationPeriod as child of AcademicPeriod | Simpler deactivation cascade, one less controller | ❌ Rejected — breaks consistency with other subdomains |
| All 4 as independent aggregate roots | More controllers/files, independent lifecycle management | ✅ Chosen — matches School/Branch pattern, enables independent CRUD |

### Decision 2: Period Overlap Validation

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Domain service | Keeps domain pure | ❌ Rejected — requires injecting repository into domain layer |
| Repository query (database-level) | Single query, fast | ❌ Rejected — leaks business logic into infra |
| Application layer (query + in-memory check) | Domain stays pure, testable | ✅ Chosen — query periods for level, check overlap in service |

Logic: `CreatePeriodService` queries active periods by `levelId`, checks `newStart < existing.end AND newEnd > existing.start`. Returns 409 on overlap.

### Decision 3: Structure Versioning

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Mutable model with version bump | Simple | ❌ Rejected — breaks immutability |
| Auto-deactivate + create new | Immutable, audit trail | ✅ Chosen — `UpdateStructureService` deactivates current, creates new with version+1 |

### Decision 4: URL Structure

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Flat (`/api/v1/academic-structures`) | Consistent with existing flat pattern | ❌ Rejected — specs mandate nested |
| Nested (`/api/v1/schools/{id}/structures`) | Expresses parent-child ownership | ✅ Chosen — follows spec requirements |

Endpoints: `/api/v1/schools/{schoolId}/structures`, `/api/v1/schools/{schoolId}/levels`, `/api/v1/levels/{levelId}/periods`, `/api/v1/periods/{periodId}/evaluation-periods`.

### Decision 5: Transaction Boundaries

`@Transactional` on write services (`CreateXxxService`, `UpdateXxxService`, `DeactivateXxxService`). Read services are non-transactional.

### Decision 6: Authorization

All write endpoints: `@PreAuthorize("hasRole('SCHOOL_ADMIN')")`. Read endpoints: `@PreAuthorize("isAuthenticated()")`. Exception: structure deactivate uses `hasAnyRole('SCHOOL_ADMIN', 'PLATFORM_ADMIN')`.

### Decision 7: Database Schema

Flyway V5–V8. `VARCHAR(36)` for UUID PKs. Enum types stored as `VARCHAR`. Unique constraints: `(school_id, version)` on structures, `(school_id, number)` on levels. Index: `(level_id, start_date, end_date)` on periods for overlap queries. Index: `(school_id, active)` on structures for active-structure lookup.

## Data Flow

```
Controller                     Service                        Repository
    │                            │                               │
    │  CreateXxxRequest          │                               │
    ├──────────────────────────►│                               │
    │                            │  validate cross-aggregate     │
    │                            │  rules + deactivation logic   │
    │                            │                               │
    │                            │  XxxAggregate.create(...)     │
    │                            │                               │
    │                            │  save(aggregate)              │
    │                            ├──────────────────────────────►│
    │                            │                               │  JPA save
    │                            │  ◄────────────────────────────┤
    │                            │                               │
    │  XxxResponse (200/201)     │                               │
    │  ◄─────────────────────────┤                               │
    │                            │                               │
```

Errors: `IllegalArgumentException` → 422/409, `IllegalStateException` → 422, `NoSuchElementException` → 404.

## File Changes

| Layer | Files | Description |
|-------|-------|-------------|
| Domain | 4 aggregates + 5 VOs ~ 16 files | `AcademicStructure`, `AcademicLevel`, `AcademicPeriod`, `EvaluationPeriod` + IDs + enums (`StructureType`, `PeriodType`) |
| Application | 4 use-case sets ~ 20 files | `port/in` interfaces, `port/out` repositories, `dto/command`, `dto/result`, `usecase/XxxService` per aggregate |
| Infrastructure | 4 entities + 4 repos + 4 adapters ~ 12 files | JPA entities, Spring Data repos, adapter mappers |
| Interfaces | 4 controllers + request/response DTOs ~ 12 files | REST controllers, `CreateXxxRequest`, `XxxResponse` records |
| Config | 1 file | `AcademicBeansConfig` wiring services |
| Migration | 4 files | `V5__academic_structures.sql` – `V8__evaluation_periods.sql` |
| Tests | ~ 20 files | Unit (domain + use case), integration (JPA repos), web-slice (controllers) |

**Total**: ~64 new files.

## Interfaces / Contracts

### Key Repository Methods (port/out)

```java
// AcademicPeriodRepository — overlap support
List<AcademicPeriod> findActiveByLevelId(AcademicLevelId levelId);

// AcademicLevelRepository — deactivation guard
boolean existsActivePeriodsByLevelId(AcademicLevelId levelId);

// AcademicStructureRepository — versioning support
Optional<AcademicStructure> findActiveBySchoolId(SchoolId schoolId);
Optional<AcademicStructure> findLatestBySchoolId(SchoolId schoolId);
```

### Exception Mapping

| Exception | HTTP Status |
|-----------|-------------|
| `IllegalArgumentException("…not found")` | 404 |
| `IllegalArgumentException("…already exists"/"duplicate"/"overlap")` | 409 |
| `IllegalStateException("…")` | 422 |
| `NoSuchElementException` | 404 |

Controllers catch exceptions and throw `ResponseStatusException` with corresponding status.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (domain) | Aggregate factory, behaviors, immutability, enum/guard invariants | JUnit 5, AssertJ. No mocks. |
| Unit (use case) | Service logic, cross-aggregate rules, overlap validation, versioning | `@ExtendWith(MockitoExtension)`, mock repos + `Clock`. Verify `save()` calls and exception paths. |
| Integration (infra) | JPA repos: save, find, unique constraints, index queries | `@DataJpaTest` with H2, TestEntityManager. Disable Flyway. |
| Web slice (interfaces) | Controller: 201/200/404/409/422/403 responses | `@WebMvcTest`, `@MockitoBean` use cases, `@WithMockUser` for auth. |

## Migration / Rollout

Flyway V5–V8 run sequentially. Deployed alongside code. Rollback: delete new packages + revert Flyway or run down-migrations. No data migration required — greenfield tables.

## Open Questions

None. All design decisions resolved.
