# Proposal: Completar Fase 1 — Auth + Usuarios

## Intent

Completar Fase 1 del roadmap para tener un backend funcional con API REST, seguridad JWT y persistencia sobre los agregados User y Membership ya implementados en dominio. Sin esto, el sistema no es desplegable ni integrable.

## Scope

### In Scope
- Flyway migrations V1 (users), V2 (memberships)
- Spring @Configuration wiring hexagonal components + Clock bean
- DTO mappers: UserMapper, MembershipMapper (Command→Domain, Domain→Result)
- REST controllers: AuthController (login/register), UserController (CRUD), MembershipController
- JWT security: JwtService, JwtAuthenticationFilter, SecurityFilterChain
- @Transactional on application services
- Request/Response DTOs for REST layer
- Tests: domain unit, application service, controller integration

### Out of Scope
- School/Branch controllers (Fase 2)
- Academic domain (Fase 2)
- Password reset / email verification
- Refresh tokens
- UserEntity refactor to immutability (separate cleanup)

## Capabilities

### New Capabilities
- `user-auth`: JWT-based login/register with password hashing, token generation, and stateless auth
- `user-management`: CRUD for users (create, get, list, activate/deactivate, block, change password)
- `membership-management`: Assign, remove, toggle memberships with role/scope validation

### Modified Capabilities
None — no existing specs to modify.

## Approach

1. **Flyway** — V1 create users table, V2 create memberships table with FK to users.
2. **Config** — Single `HexagonalConfig` class scanning ports, services, adapters; `Clock` bean.
3. **Mappers** — `UserMapper`/`MembershipMapper` in `application/` layer, injected into services.
4. **Security** — `JwtService` (sign/verify), `JwtAuthenticationFilter` (OncePerRequest), `SecurityFilterChain` permitting `/auth/**`, securing `/api/**`.
5. **Controllers** — `AuthController` (POST login/register), `UserController` (CRUD), `MembershipController` (assign/remove/toggle). All under `/api/v1`.
6. **Transactional** — `@Transactional` on every application service method.
7. **Tests** — Domain unit tests (aggregate behaviors, VO validation), service tests (mocked repos), `@WebMvcTest` controller tests, `@DataJpaTest` persistence.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `src/main/resources/db/` | New | V1__create_users_table.sql, V2__create_memberships_table.sql |
| `infrastructure/config/` | New | HexagonalConfig.java, ClockConfig.java |
| `application/user/` | Modified | Add UserMapper, annotate services with @Transactional |
| `application/membership/` | Modified | Add MembershipMapper, annotate services with @Transactional |
| `infrastructure/security/` | New | JwtService.java, JwtAuthenticationFilter.java, SecurityConfig.java |
| `interfaces/http/controller/` | New | AuthController.java, UserController.java, MembershipController.java |
| `interfaces/http/request/` | New | LoginRequest, RegisterRequest, etc. |
| `interfaces/http/response/` | New | AuthResponse, UserResponse, etc. |
| `src/test/java/` | New | Domain unit tests, service tests, controller tests |
| `build.gradle.kts` | Modified | Add spring-security dependencies if missing |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| JWT secret hardcoded | Medium | Externalize via `application.yaml`, document secret rotation |
| @Transactional on read ops causes perf issues | Low | Mark read-only methods with `@Transactional(readOnly = true)` |
| Partial failure in CreateUserService (user + membership) | High | Wrap in single `@Transactional`, handle rollback explicitly |

## Rollback Plan

- **DB**: Run `FLYWAY UNDO` or manually drop both tables and set Flyway baseline back.
- **Code**: `git revert` the merge commit for this change.
- **Config**: Restore previous `application.yaml` and `build.gradle.kts`.
- **Verification**: Run full test suite and smoke-test login endpoint.

## Dependencies

- Spring Boot Starter Security (add to `build.gradle.kts`)
- `io.jsonwebtoken:jjwt-api/impl/jackson` for JWT

## Success Criteria

- [ ] All CRUD operations for users return correct HTTP status codes (201, 200, 204, 404)
- [ ] Login returns JWT; protected endpoints reject unauthenticated requests (401)
- [ ] Membership assignment validates role scope compatibility
- [ ] Flyway migrations apply clean on fresh PostgreSQL
- [ ] Test suite passes: `./gradlew test` — unit + integration
