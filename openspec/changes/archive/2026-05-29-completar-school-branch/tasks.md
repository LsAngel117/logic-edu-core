# Tasks: completar-school-branch

## Review Workload Forecast

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: feature-branch-chain
400-line budget risk: High

| Field | Value |
|-------|-------|
| Estimated changed lines | 2800-3200 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1: School subdomain → PR 2: Branch subdomain |
| Delivery strategy | ask-on-risk |
| Chain strategy | feature-branch-chain |

### Suggested Work Units

| Unit | Goal | Likely PR | Base | Est. Files | Est. Lines |
|------|------|-----------|------|------------|------------|
| 1 | School subdomain (V3 + infra + app + rest + tests) | PR 1 | main | ~31 | ~1300 |
| 2 | Branch subdomain (V4 + infra + app + rest + tests) | PR 2 | PR 1 branch | ~32 | ~1300 |

## Phase 1: Foundation (Flyway + JPA + Adapters + Output Ports)

- [x] 1.1 `V3__create_schools_table.sql` — DDL + UNIQUE(name), UNIQUE(code)
- [x] 1.2 `V4__create_branches_table.sql` — DDL + FK(school_id) + UNIQUE(school_id, name) + partial unique idx_branches_main
- [x] 1.3 `SchoolEntity.java` — JPA entity for schools table
- [x] 1.4 `BranchEntity.java` — JPA entity for branches table
- [x] 1.5 `SchoolJpaRepository.java` — Spring Data with findBy/ existsBy methods
- [x] 1.6 `BranchJpaRepository.java` — Spring Data with findById/ findBySchoolId/ countMainBySchool/ existsActiveBySchool
- [x] 1.7 `school/port/out/SchoolRepository.java` — save, findById, findAll, existsByName, existsByCode (existsActiveById not needed for this PR)
- [x] 1.8 `branch/port/out/BranchRepository.java` — save, findById, findBySchoolId, countMainBySchool, existsActiveBySchool
- [x] 1.9 `SchoolRepositoryAdapter.java` — mapToEntity/ mapToDomain, implements SchoolRepository
- [x] 1.10 `BranchRepositoryAdapter.java` — mapToEntity/ mapToDomain, implements BranchRepository

## Phase 2: Application Layer (Ports + DTOs + Use Cases)

- [x] 2.1 School input ports (5): `CreateSchoolUseCase`, `GetSchoolUseCase`, `ListSchoolsUseCase`, `UpdateSchoolUseCase`, `DeactivateSchoolUseCase`
- [x] 2.2 Branch input ports (5): `CreateBranchUseCase`, `GetBranchUseCase`, `ListBranchesBySchoolUseCase`, `UpdateBranchUseCase`, `DeactivateBranchUseCase`
- [x] 2.3 `CreateSchoolCommand` / `UpdateSchoolCommand` / `SchoolResult` — records with domain VOs + `from(School)` factory
- [x] 2.4 `CreateBranchCommand` / `UpdateBranchCommand` / `BranchResult` — records with domain VOs + `from(Branch)` factory
- [x] 2.5 `CreateSchoolService` — @Transactional; validates unique code/name
- [x] 2.6 `GetSchoolService` / `ListSchoolsService` — @Transactional(readOnly)
- [x] 2.7 `UpdateSchoolService` — @Transactional; rejects INACTIVE school
- [x] 2.8 `DeactivateSchoolService` — @Transactional; branch guard TODO for PR #2
- [x] 2.9 `CreateBranchService` — @Transactional; validates school active, MAIN uniqueness, type-address rules
- [x] 2.10 `GetBranchService` / `ListBranchesBySchoolService` — @Transactional(readOnly); school-scoped
- [x] 2.11 `UpdateBranchService` — @Transactional; validates active + MAIN uniqueness on type change
- [x] 2.12 `DeactivateBranchService` — @Transactional; guards if MAIN has active secondaries

## Phase 3: Configuration + REST

- [x] 3.1 `SchoolBeansConfig` — wire School use cases (match UserBeansConfig pattern)
- [x] 3.2 `BranchBeansConfig` — wire Branch use cases
- [x] 3.3 `PersistenceConfig` — created with SchoolRepositoryAdapter (and existing User/Membership adapters)
- [x] 3.4 School REST DTOs — `CreateSchoolRequest`, `UpdateSchoolRequest`, `SchoolResponse` (String records)
- [x] 3.5 Branch REST DTOs — `CreateBranchRequest`, `UpdateBranchRequest`, `BranchResponse`
- [x] 3.6 `SchoolController` — POST/ GET/ GET{id}/ PUT{id}/ PATCH{id}/deactivate under `/api/v1/schools`
- [x] 3.7 `BranchController` — nested under `/api/v1/schools/{schoolId}/branches`; same operations

## Phase 4: Tests

- [x] 4.1 Branch domain test — VIRTUAL with address → exception; MAIN/SECONDARY without address → exception
- [x] 4.2 `CreateSchoolServiceTest` — success, duplicate code, duplicate name, optional fields
- [x] 4.3 `GetSchoolServiceTest` / `ListSchoolsServiceTest` — found/ not-found/ list
- [x] 4.4 `UpdateSchoolServiceTest` — success, INACTIVE rejection, not-found
- [x] 4.5 `DeactivateSchoolServiceTest` — success, already-inactive (idempotent), not-found
- [x] 4.6 `CreateBranchServiceTest` — success, INACTIVE school (422), duplicate MAIN (409), VIRTUAL+address (422), PHYSICAL-address (422)
- [x] 4.7 `GetBranchServiceTest` / `ListBranchesBySchoolServiceTest` — school scoping (404 for wrong school)
- [x] 4.8 `UpdateBranchServiceTest` — success, INACTIVE rejection (422), MAIN uniqueness on change (409)
- [x] 4.9 `DeactivateBranchServiceTest` — success, MAIN with active secondaries guard (422)
- [x] 4.10 `SchoolJpaRepositoryTest` — @DataJpaTest; unique constraints, CRUD, existsBy
- [x] 4.11 `BranchJpaRepositoryTest` — @DataJpaTest; FK constraint, partial unique, queries
- [x] 4.12 `SchoolControllerTest` — MockMvc; 201/ 200/ 404/ 409/ 422 per spec scenarios
- [x] 4.13 `BranchControllerTest` — MockMvc; 201/ 404/ 409/ 422/ 403 per spec scenarios

## Phase 5: Verify Fixes (CRITICAL issues from verify report)

- [x] 5.1 Add explicit `type` field to `CreateBranchRequest` and `UpdateBranchRequest`; remove `BranchController.resolveType()` business logic inference
- [x] 5.2 Implement authorization: add `@PreAuthorize` annotations to SchoolController and BranchController; create minimal SecurityConfig with `@EnableMethodSecurity`
- [x] 5.3 Add `GlobalExceptionHandler` to map exceptions to proper HTTP status codes (IllegalStateException→422, NoSuchElementException→404, AccessDeniedException→403, etc.)
- [x] 5.4 Create `GlobalExceptionHandlerTest` — test all exception→status mappings
- [x] 5.5 Update `BranchControllerTest` — add type field to requests, add SECONDARY/TEMPORARY/invalid type tests, add 403 authorization tests, convert to @WebMvcTest
- [x] 5.6 Update `SchoolControllerTest` — add @WithMockUser, add 403 authorization tests, convert to @WebMvcTest
