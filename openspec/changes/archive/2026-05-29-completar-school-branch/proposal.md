# Proposal: Completar School + Branch Layers

## Intent

Completar Application, Infrastructure e Interfaces para School y Branch (Phase 2, Part 1). Sin esto el sistema no puede gestionar escuelas y sedes.

## Scope

### In Scope
- Flyway V3 (schools), V4 (branches FK→schools)
- 10 use cases: School create/get/list/update/deactivate; Branch create/get/list-by-school/update/deactivate
- Output ports, DTOs, mappers, JPA entities, adapters (follow Phase 1 pattern)
- REST: `/api/v1/schools`, `/api/v1/schools/{schoolId}/branches`
- Config: SchoolBeansConfig, BranchBeansConfig, update PersistenceConfig
- Rules: unique name/code, active school guard, MAIN uniqueness, type validation

### Out of Scope
- Academic terms, courses, enrollments (Phase 2, Part 2)
- Bulk import/export, soft-delete, branch reassignment

## Capabilities

### New
- `school-management`: CRUD + deactivate, unique name/code, status lifecycle
- `branch-management`: CRUD + deactivate, nested under school, MAIN uniqueness, type rules

### Modified
None.

## Approach

1. **Flyway** — V3 schools (id, name, code, short_name, desc, email, phone, address, status, timestamps). V4 branches (+ school_id FK, type, UNIQUE per school).
2. **Application** — `*UseCase` interfaces, `*Service` with `@Transactional`, Commands/Results with `from()` factory.
3. **Infrastructure** — JPA entities, Spring Data repos, adapters with mapToEntity/mapToDomain. Branch adapter validates school is active. MAIN uniqueness at app layer + DB partial index.
4. **Interfaces** — Controllers return `ResponseEntity`, no wrappers.
5. **Config** — One `*BeansConfig` per subdomain, PersistenceConfig adds repos.

## Areas

| Area | Impact |
|------|--------|
| `resources/db/migration/V3__*.sql, V4__*.sql` | New |
| `application/school/`, `application/branch/` | New |
| `infrastructure/school/persistence/`, `infrastructure/branch/persistence/` | New |
| `infrastructure/config/*BeansConfig.java` (×2) | New |
| `infrastructure/config/PersistenceConfig.java` | Modified |
| `interfaces/http/controller/school/`, `branch/` | New |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| MAIN uniqueness race | Low | App check + DB partial unique index on (school_id, type) WHERE type='MAIN' |
| Branch on inactive school | Low | BranchService reads SchoolRepository before create |
| Name/code collision race | Low | App validate + DB unique constraints |

## Rollback

- **DB**: DROP branches, schools; reset Flyway baseline.
- **Code**: `git revert` merge commit.

## Dependencies

None beyond existing stack.

## Success Criteria

- [ ] V3/V4 apply clean; FK + unique constraints enforced
- [ ] School CRUD correct statuses; duplicate name/code → 409
- [ ] Branch on active school → 201; on inactive school → 422
- [ ] Second MAIN per school → 409
- [ ] VIRTUAL + address → 422; PHYSICAL no address → 422
- [ ] Unknown school/branch → 404
- [ ] `./gradlew test` passes
