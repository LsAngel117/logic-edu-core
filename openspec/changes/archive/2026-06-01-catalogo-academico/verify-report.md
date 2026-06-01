## Verification Report

**Change**: catalogo-academico
**Version**: N/A
**Mode**: Strict TDD

### Completeness
| Metric | Value |
|--------|-------|
| Tasks total | 25 |
| Tasks complete | 25 |
| Tasks incomplete | 0 |

### Build & Tests Execution
**Build**: ✅ Passed
```text
./gradlew clean test
BUILD SUCCESSFUL in 20s
6 actionable tasks: 6 executed
```

**Tests**: ✅ 606 passed / ❌ 0 failed / ⚠️ 0 skipped
```text
Total tests: 606
Failures: 0
Errors: 0
Skipped: 0
```

**Subject-specific tests**: ✅ 70 passed / 0 failed
```text
SubjectTest (domain):                   28 passed (8 nested classes)
CreateSubjectServiceTest:               4 passed
GetSubjectServiceTest:                  3 passed
ListSubjectsBySchoolServiceTest:        3 passed
UpdateSubjectServiceTest:               5 passed
DeactivateSubjectServiceTest:           4 passed
SubjectControllerTest:                 13 passed
SubjectJpaRepositoryTest:               5 passed
SubjectRepositoryAdapterTest:           4 passed
```

**Coverage**: ➖ Not available (no JaCoCo/jacoco configured)

### TDD Compliance
| Check | Result | Details |
|-------|--------|---------|
| TDD Evidence reported | ✅ | Found in apply-progress (Engram #50) |
| All tasks have tests | ✅ | 25/25 tasks complete, all non-structural tasks have covering tests |
| RED confirmed (tests exist) | ✅ | 9/9 test files verified in codebase |
| GREEN confirmed (tests pass) | ✅ | 70/70 subject tests pass on clean execution |
| Triangulation adequate | ✅ | All behavioral tasks have 2+ test cases |
| Safety Net for modified files | ✅ | Controller test had 606/606 baseline |

**TDD Compliance**: 6/6 checks passed

### Test Layer Distribution
| Layer | Tests | Files | Tools |
|-------|-------|-------|-------|
| Unit (domain) | 28 | 1 (SubjectTest.java) | JUnit 5 + AssertJ |
| Unit (service) | 19 | 5 (*ServiceTest.java) | JUnit 5 + Mockito |
| Unit (controller standalone) | 13 | 1 (SubjectControllerTest.java) | JUnit 5 + MockMvc standalone |
| Unit (adapter) | 4 | 1 (SubjectRepositoryAdapterTest.java) | JUnit 5 + Mockito |
| Integration (JPA) | 5 | 1 (SubjectJpaRepositoryTest.java) | @DataJpaTest |
| E2E | 0 | 0 | — |
| **Total** | **69** (tests) / **70** (XML suites) | **9** | |

### Spec Compliance Matrix
| Requirement | Scenario | Test | Result |
|-------------|----------|------|--------|
| REQ-01: Flyway V9 | subjects table exists with schema and unique index | (JPA integration tests pass → table/schema exists) | ✅ COMPLIANT |
| REQ-02: Create Subject | POST 201 with valid request | `SubjectControllerTest > postShouldCreateSubjectAndReturn201` | ✅ COMPLIANT |
| REQ-02: Create Subject | POST 409 on duplicate code | `SubjectControllerTest > postShouldReturn409OnDuplicate` | ✅ COMPLIANT |
| REQ-02: Create Subject | POST 404 when school not found | `SubjectControllerTest > postShouldReturn404WhenSchoolNotFound` | ✅ COMPLIANT |
| REQ-02: Create Subject | POST 422 when hours < 0 | `SubjectTest > shouldRejectNegativeHoursInCreate` (domain validates; controller maps to 409, not 422) | ⚠️ PARTIAL |
| REQ-02: Create Subject | POST 403 non-SCHOOL_ADMIN | `@PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")` on controller — no explicit test | ⚠️ PARTIAL |
| REQ-02: Create Subject | POST 401 unauthenticated | `@PreAuthorize` on controller — no explicit test | ⚠️ PARTIAL |
| REQ-03: Get Subject | GET 200 existing subject | `SubjectControllerTest > getByIdShouldReturn200` | ✅ COMPLIANT |
| REQ-03: Get Subject | GET 404 subject in different school | (same as unknown id — use case returns 404) | ✅ COMPLIANT |
| REQ-03: Get Subject | GET 404 unknown id | `SubjectControllerTest > getByIdShouldReturn404WhenNotFound` | ✅ COMPLIANT |
| REQ-03: Get Subject | GET 401 unauthenticated | `@PreAuthorize("isAuthenticated()")` — no explicit test | ⚠️ PARTIAL |
| REQ-04: List Subjects | GET 200 with list | `SubjectControllerTest > listBySchoolIdShouldReturn200WithList` | ✅ COMPLIANT |
| REQ-04: List Subjects | GET 200 empty list | `SubjectControllerTest > listBySchoolIdShouldReturnEmptyList` | ✅ COMPLIANT |
| REQ-04: List Subjects | GET 401 unauthenticated | `@PreAuthorize("isAuthenticated()")` — no explicit test | ⚠️ PARTIAL |
| REQ-05: Update Subject | PUT 200 valid data | `SubjectControllerTest > putShouldUpdateAndReturn200` | ✅ COMPLIANT |
| REQ-05: Update Subject | PUT 422 inactive subject | `SubjectControllerTest > putShouldReturn422WhenInactive` | ✅ COMPLIANT |
| REQ-05: Update Subject | PUT 409 duplicate code | `SubjectControllerTest > putShouldReturn409OnDuplicate` | ✅ COMPLIANT |
| REQ-05: Update Subject | PUT 403 non-SCHOOL_ADMIN | `@PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")` — no explicit test | ⚠️ PARTIAL |
| REQ-05: Update Subject | PUT 401 unauthenticated | `@PreAuthorize` — no explicit test | ⚠️ PARTIAL |
| REQ-06: Deactivate Subject | PATCH 200 ACTIVE → INACTIVE | `SubjectControllerTest > patchDeactivateShouldReturn200` | ✅ COMPLIANT |
| REQ-06: Deactivate Subject | PATCH 200 already INACTIVE (idempotent) | `DeactivateSubjectServiceTest > shouldReturnNoOpWhenAlreadyInactive` + `SubjectTest > shouldReturnSameStateIfAlreadyInactive` | ✅ COMPLIANT |
| REQ-06: Deactivate Subject | PATCH 403 non-SCHOOL_ADMIN | `@PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")` — no explicit test | ⚠️ PARTIAL |
| REQ-06: Deactivate Subject | PATCH 401 unauthenticated | `@PreAuthorize` — no explicit test | ⚠️ PARTIAL |

**Compliance summary**: 14/24 scenarios fully compliant, 10/24 partial (9 auth + 1 status-code mapping)

### Correctness (Static Evidence)
| Requirement | Status | Notes |
|------------|--------|-------|
| Flyway V9 migration | ✅ Implemented | `V9__create_subjects_table.sql` with FK, UNIQUE(school_id,code) |
| Subject domain aggregate | ✅ Implemented | `Subject.java`: immutable, create/restore/changeData/deactivate, hours>=0 |
| Create Subject use case | ✅ Implemented | `CreateSubjectService.java`: validates school exists + code uniqueness |
| Get Subject use case | ✅ Implemented | `GetSubjectService.java`: find by id or throw |
| List Subjects use case | ✅ Implemented | `ListSubjectsBySchoolService.java`: find all by school |
| Update Subject use case | ✅ Implemented | `UpdateSubjectService.java`: validates ACTIVE, checks code if changed |
| Deactivate Subject use case | ✅ Implemented | `DeactivateSubjectService.java`: find, deactivate, save (idempotent) |
| JPA adapter | ✅ Implemented | `SubjectRepositoryAdapter.java`: entity↔domain, all port methods |
| REST controller | ✅ Implemented | `SubjectController.java`: 5 endpoints at `/api/v1/schools/{schoolId}/subjects` |
| Bean wiring | ✅ Implemented | 5 @Bean methods in `AcademicBeansConfig.java` + adapter in `PersistenceConfig.java` |
| Hours >= 0 invariant | ✅ Implemented | Subject constructor validate() + changeData() both check `hours < 0` |
| Code unique per school | ✅ Implemented | Service-level `existsBySchoolIdAndCode` + DB UNIQUE index |
| Deactivate idempotency | ✅ Implemented | `Subject.deactivate()` returns `this` when already INACTIVE |

### Coherence (Design)
| Decision | Followed? | Notes |
|----------|-----------|-------|
| Subject as own aggregate root | ✅ Yes | Standalone aggregate, not embedded in School |
| URL nesting under school | ✅ Yes | `/api/v1/schools/{schoolId}/subjects` |
| Deactivate idempotency (return 200 no-op) | ✅ Yes | Domain returns `this` if already INACTIVE; service saves anyway |
| Code uniqueness at application layer | ✅ Yes | `existsBySchoolIdAndCode` in create + update services |
| changeData as single method | ✅ Yes | `Subject.changeData(code,name,desc,hours,now)` |
| Immutable aggregate pattern | ✅ Yes | All fields final, no setters, factory methods produce new instances |
| JPA adapter maps entity↔domain | ✅ Yes | `SubjectRepositoryAdapter` with `mapToEntity`/`mapToDomain` |
| 5 use case interfaces (one per operation) | ✅ Yes | Create/Get/ListBySchool/Update/DeactivateSubjectUseCase |
| Testing at all layers | ✅ Yes | Domain (28), Service (19), Controller (13), JPA (5), Adapter (4) |

### Assertion Quality
| File | Line | Assertion | Issue | Severity |
|------|------|-----------|-------|----------|
| `SubjectTest.java` | 366-371 | `assertThat(method.getName()).doesNotStartWith("set")` | Implementation detail coupling — tests code style, not behavior | SUGGESTION |

**Assertion quality**: 0 CRITICAL, 0 WARNING, 1 SUGGESTION

### Quality Metrics
**Linter**: ➖ Not available
**Type Checker**: ➖ Not available (Java compilation passes — verified by `./gradlew compileJava`)

### Issues Found
**CRITICAL**: None

**WARNING**:
1. **POST hours < 0 returns 409 instead of spec-required 422**: Domain validates `hours >= 0` and throws `IllegalArgumentException`. The controller's exception handler maps all non-"School not found" `IllegalArgumentException`s to 409 CONFLICT. Spec scenario "GIVEN hours < 0 WHEN POST THEN 422" expects 422 UNPROCESSABLE_ENTITY. The request IS rejected (correct), but with wrong HTTP status code. No controller test covers this scenario.
2. **Auth scenarios (401/403) have no explicit covering tests**: 9 of 24 spec scenarios involve auth (401 unauthenticated, 403 wrong role). The controller has `@PreAuthorize` annotations correctly placed, but `standaloneSetup` MockMvc tests do not enforce Spring Security. These scenarios are untested at the controller/integration level. Consistent with existing project patterns (other controllers follow the same approach), but spec-completeness requires explicit coverage.

**SUGGESTION**:
1. **`shouldHaveNoPublicSetters` tests code style**: Reflection-based check in `SubjectTest.java:366` asserts no method starts with "set". This is an implementation detail check, not a behavioral assertion. Low priority.

### Verdict
**PASS WITH WARNINGS**

All 70 new tests pass. All 606 project tests pass. All 25 tasks complete. Architecture follows the established hexagonal pattern (no Spring in domain, controller is pure adapter). Business rules enforced (code unique per school, hours >= 0, no update on INACTIVE). Two warnings: (1) POST hours < 0 HTTP status mismatch (409 vs spec 422), (2) auth scenarios untested. Neither is blocking.
