# Design: Completar School + Branch Layers

## Technical Approach

Extend Phase 1 hexagonal patterns to School (aggregate root) and Branch (child within School). Flyway V3 → V4, then application ports/services, JPA adapters, REST controllers. Branch is nested under School (`/api/v1/schools/{schoolId}/branches`). All domain models exist — this phase builds the surrounding layers.

## Architecture Decisions

| Decision | Options | Choice | Rationale |
|----------|---------|--------|-----------|
| Branch URL pattern | `/api/v1/branches` vs `/api/v1/schools/{id}/branches` | Nested under school | REST convention; branch ownership clear in URL |
| MAIN uniqueness enforcement | App-layer only vs App + DB partial index | App check + DB partial unique index `WHERE type='MAIN'` | Race condition protection; PostgreSQL-specific |
| Update granularity | Single PUT endpoint vs per-field PATCH | Single PUT (School.changeData / Branch composite methods) | Domain enforces atomically via factory (immutable aggregates); matches Phase 1 patterns |
| Deactivation guard: school | Check active branches in use case vs domain | Application-layer check (count active branches) | Domain cannot reach outside aggregate; app layer orchestrates cross-aggregate rules |
| Deactivation guard: branch | Check if MAIN has active secondaries | App-layer check | Same reasoning as school deactivation |
| Transaction strategy | `@Transactional` on service methods | Per-use-case transactions on mutable ops, readOnly on reads | Matches Phase 1; atomic domain ops |
| Branch code uniqueness | Global vs per-school | Not enforced (spec has UNIQUE on name only) | Code is descriptive, not a uniqueness constraint per spec |
| Config granularity | One BeansConfig vs per-subdomain | One per subdomain (SchoolBeansConfig, BranchBeansConfig) | Match Phase 1 UserBeansConfig pattern |

## Schema Design

### V3 — `schools`
```
id VARCHAR(36) PK | name VARCHAR NOT NULL UNIQUE | code VARCHAR NOT NULL UNIQUE
short_name VARCHAR NOT NULL | description VARCHAR | email VARCHAR | phone VARCHAR
address VARCHAR | status VARCHAR(10) NOT NULL | created_at TIMESTAMPTZ NOT NULL
updated_at TIMESTAMPTZ NOT NULL
```
Indexes: UNIQUE(name), UNIQUE(code).

### V4 — `branches`
```
id VARCHAR(36) PK | school_id VARCHAR(36) NOT NULL REFERENCES schools(id)
name VARCHAR NOT NULL | code VARCHAR NOT NULL | short_name VARCHAR NOT NULL
description VARCHAR | email VARCHAR | phone VARCHAR | address VARCHAR
type VARCHAR(10) NOT NULL | status VARCHAR(10) NOT NULL
created_at TIMESTAMPTZ NOT NULL | updated_at TIMESTAMPTZ NOT NULL
```
Indexes: UNIQUE(school_id, name), INDEX(school_id).
Partial unique: `CREATE UNIQUE INDEX idx_branches_main ON branches(school_id) WHERE type = 'MAIN'`

## Data Flow

### Create Branch (transactional boundary)
```
Controller → CreateBranchRequest (raw strings)
  → CreateBranchCommand (domain VOs: SchoolId, BranchId, BranchName, etc.)
  → CreateBranchService:
      1. SchoolRepository.findById(schoolId) → throws 404 if absent
      2. If !school.isActive() → throws IllegalStateException (→ 422)
      3. If type==MAIN && branchRepository.countMainBySchool(schoolId) > 0 → throws (→ 409)
      4. Branch.create(...) → domain validates type/address rules
      5. branchRepository.save(branch)
      6. Return BranchResult (with from() factory)
  → Controller maps to BranchResponse → 201
```

### Deactivate School (transactional boundary)
```
Controller → DeactivateSchoolUseCase.execute(schoolId)
  → DeactivateSchoolService:
      1. SchoolRepository.findById → throws 404 if absent
      2. branchRepository.existsActiveBySchool(schoolId) → if true, throw (→ 422)
      3. school.deactivate(now) → School domain method (immutable new instance)
      4. schoolRepository.save(school)
```

### Update Branch (transactional boundary)
```
Controller → UpdateBranchRequest
  → UpdateBranchCommand (with VOs)
  → UpdateBranchService:
      1. SchoolRepository.findById → validate school exists + active
      2. BranchRepository.findById → throws 404 if branch not found or wrong school
      3. Validate branch is active
      4. If type changes to MAIN → check uniqueness (countMainBySchool excluding self)
      5. Branch.changeBasicInfo(...) or changeContactInfo(...) → immutable new instance
      6. branchRepository.save(updated)
      7. Return BranchResult
```

## Transaction Strategy

| Operation | Isolation | Read-only |
|-----------|-----------|-----------|
| Create/Update/Deactivate School | Default | No |
| GetSchool, ListSchools | Default | Yes |
| Create/Update/Deactivate Branch | Default | No |
| GetBranch, ListBranchesBySchool | Default | Yes |

`@Transactional` on every service class that modifies state; `@Transactional(readOnly = true)` on reads.

## File Changes

### New Files (26)

| File | Description |
|------|-------------|
| `src/main/resources/db/migration/V3__create_schools_table.sql` | Schools DDL + indexes |
| `src/main/resources/db/migration/V4__create_branches_table.sql` | Branches DDL + FK + indexes + partial unique |
| `application/school/port/in/CreateSchoolUseCase.java` | Input port |
| `application/school/port/in/GetSchoolUseCase.java` | Input port |
| `application/school/port/in/ListSchoolsUseCase.java` | Input port |
| `application/school/port/in/UpdateSchoolUseCase.java` | Input port |
| `application/school/port/in/DeactivateSchoolUseCase.java` | Input port |
| `application/school/port/out/SchoolRepository.java` | Output port (save, findById, findAll, existsByName, existsByCode, existsActiveById) |
| `application/school/dto/command/CreateSchoolCommand.java` | Record with domain VOs |
| `application/school/dto/command/UpdateSchoolCommand.java` | Record with domain VOs |
| `application/school/dto/result/SchoolResult.java` | Record + from(School) |
| `application/school/usecase/CreateSchoolService.java` | @Transactional |
| `application/school/usecase/GetSchoolService.java` | @Transactional(readOnly) |
| `application/school/usecase/ListSchoolsService.java` | @Transactional(readOnly) |
| `application/school/usecase/UpdateSchoolService.java` | @Transactional |
| `application/school/usecase/DeactivateSchoolService.java` | @Transactional |
| `application/branch/port/in/CreateBranchUseCase.java` | Input port |
| `application/branch/port/in/GetBranchUseCase.java` | Input port |
| `application/branch/port/in/ListBranchesBySchoolUseCase.java` | Input port |
| `application/branch/port/in/UpdateBranchUseCase.java` | Input port |
| `application/branch/port/in/DeactivateBranchUseCase.java` | Input port |
| `application/branch/port/out/BranchRepository.java` | Output port (save, findById, findBySchoolId, countMainBySchool, existsActiveBySchool) |
| `application/branch/dto/command/CreateBranchCommand.java` | Record with domain VOs |
| `application/branch/dto/command/UpdateBranchCommand.java` | Record with domain VOs (optional update fields) |
| `application/branch/dto/result/BranchResult.java` | Record + from(Branch) |
| `application/branch/usecase/CreateBranchService.java` | @Transactional; validates school active + MAIN uniqueness |
| `application/branch/usecase/GetBranchService.java` | @Transactional(readOnly); validates belongs to school |
| `application/branch/usecase/ListBranchesBySchoolService.java` | @Transactional(readOnly) |
| `application/branch/usecase/UpdateBranchService.java` | @Transactional |
| `application/branch/usecase/DeactivateBranchService.java` | @Transactional; validates no active secondaries if MAIN |
| `infrastructure/school/persistence/entity/SchoolEntity.java` | JPA entity with getters/setters |
| `infrastructure/school/persistence/repository/SchoolJpaRepository.java` | Spring Data; findBy methods |
| `infrastructure/school/persistence/adapter/SchoolRepositoryAdapter.java` | mapToEntity/mapToDomain + implements SchoolRepository |
| `infrastructure/branch/persistence/entity/BranchEntity.java` | JPA entity with getters/setters |
| `infrastructure/branch/persistence/repository/BranchJpaRepository.java` | Spring Data; findBy + count queries |
| `infrastructure/branch/persistence/adapter/BranchRepositoryAdapter.java` | mapToEntity/mapToDomain + implements BranchRepository |
| `infrastructure/config/SchoolBeansConfig.java` | School use case beans |
| `infrastructure/config/BranchBeansConfig.java` | Branch use case beans |
| `interfaces/rest/school/controller/SchoolController.java` | POST/GET/GET{id}/PUT{id}/PATCH{id}/deactivate |
| `interfaces/rest/school/dto/request/CreateSchoolRequest.java` | Record (String fields) |
| `interfaces/rest/school/dto/request/UpdateSchoolRequest.java` | Record (String fields) |
| `interfaces/rest/school/dto/response/SchoolResponse.java` | Record (flattened Strings) |
| `interfaces/rest/branch/controller/BranchController.java` | POST/GET/GET{id}/PUT{id}/PATCH{id}/deactivate under schools/{schoolId} |
| `interfaces/rest/branch/dto/request/CreateBranchRequest.java` | Record |
| `interfaces/rest/branch/dto/request/UpdateBranchRequest.java` | Record |
| `interfaces/rest/branch/dto/response/BranchResponse.java` | Record |

### Modified Files (1)

| File | Change |
|------|--------|
| `infrastructure/config/PersistenceConfig.java` | Add SchoolRepositoryAdapter and BranchRepositoryAdapter beans |

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit — Domain | Branch type/address validation | Given various BranchType+address combos, assert exceptions per domain rules |
| Unit — App | School/Branch services | Mock repositories; verify orchestration, validation, transactional boundaries |
| Integration | JPA repositories | `@DataJpaTest` with Flyway auto-apply; test unique constraints, FK, queries |
| Integration | REST controllers | `@WebMvcTest` or `@SpringBootTest`; test HTTP status codes (201/404/409/422) |
| E2E | Full CRUD flows | `@SpringBootTest` with test DB; create school → create branch → update → deactivate |

## Open Questions

- None — all design decisions resolved per proposal/spec and Phase 1 patterns.
