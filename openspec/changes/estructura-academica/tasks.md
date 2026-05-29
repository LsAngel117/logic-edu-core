# Tasks: Estructura Académica

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~4200–4800 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR1: Phase 0+1 → PR2: Phase 2 → PR3: Phase 3 → PR4: Phase 4 |
| Delivery strategy | ask-on-risk |
| Chain strategy | stacked-to-main |

Decision needed before apply: Yes (resolved: stacked-to-main, 4 PRs)
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Base | Notes |
|------|------|-----------|------|-------|
| 1 | Foundation + AcademicStructure | PR #1 | main | Migrations V5-V8 + full AcademicStructure stack (incl. config wiring) |
| 2 | AcademicLevel | PR #2 | main | Full AcademicLevel stack. FK depends on Phase 0 only, not Phase 1 |
| 3 | AcademicPeriod (overlap) | PR #3 | main | Overlap validation via `findActiveByLevelId`. FK depends on Phase 0 |
| 4 | EvaluationPeriod (weight) | PR #4 | main | Weight-sum validation. FK depends on Phase 0 |

All 4 concepts are independent aggregate roots sharing only the Flyway foundation. Each PR is **autonomous** — no cross-concept code dependencies. Stacked-to-main works well here because the FK chains are database-only (school_id/level_id/period_id references), satisfied once Phase 0 ships.

## Phase 0: Foundation — Flyway Migrations

- [x] 0.1 Create `V5__create_academic_structures.sql` — id, school_id, structure_type, levels_count, periods_per_level, evaluation_periods_per_period, subjects_per_period, hours_per_subject, active, version, created_at, updated_at. UNIQUE(school_id, version), INDEX(school_id, active)
- [x] 0.2 Create `V6__create_academic_levels.sql` — id, school_id, name, number, status, created_at, updated_at. UNIQUE(school_id, number)
- [x] 0.3 Create `V7__create_academic_periods.sql` — id, level_id, period_type, name, sequence, start_date, end_date, status, created_at, updated_at. INDEX(level_id, start_date, end_date)
- [x] 0.4 Create `V8__create_evaluation_periods.sql` — id, period_id, name, sequence, weight, start_date, end_date, status, created_at, updated_at. INDEX(period_id)

## Phase 1: AcademicStructure

- [x] 1.1 Domain: `academic/structure/model/StructureType` enum, `AcademicStructureId` VO, `AcademicStructure` aggregate (create/restore/newVersion/deactivate), unit tests
- [x] 1.2 App port/in: `CreateStructureUseCase`, `GetStructureUseCase`, `UpdateStructureUseCase`, `DeactivateStructureUseCase`
- [x] 1.3 App port/out: `AcademicStructureRepository` — findActiveBySchoolId/ findLatestBySchoolId/save
- [x] 1.4 App DTOs: `CreateStructureCommand`, `UpdateStructureCommand`, `StructureResult`
- [x] 1.5 App services: `CreateStructureService` (one-active guard), `GetStructureService`, `UpdateStructureService` (deactivate + new version), `DeactivateStructureService` — plus Mockito tests
- [x] 1.6 Infra: `AcademicStructureEntity`, `AcademicStructureJpaRepository`, `AcademicStructureRepositoryAdapter` — plus `@DataJpaTest`
- [x] 1.7 REST DTOs: `CreateStructureRequest`, `UpdateStructureRequest`, `StructureResponse`
- [x] 1.8 REST: `StructureController` under `/api/v1/schools/{schoolId}/structures` — plus `@WebMvcTest`

## Phase 2: AcademicLevel

- [x] 2.1 Domain: `academic/level/model/AcademicLevelId` VO, `AcademicLevel` aggregate (create/restore/changeData/deactivate), unit tests
- [x] 2.2 App port/in: `CreateLevelUseCase`, `GetLevelUseCase`, `ListLevelsUseCase`, `UpdateLevelUseCase`, `DeactivateLevelUseCase`
- [x] 2.3 App port/out: `AcademicLevelRepository` — findById/findAllBySchoolId/ existsActivePeriodsByLevelId/save
- [x] 2.4 App DTOs + services: commands, results, all 5 services — Mockito tests covering duplicate number, deactivation with active periods
- [x] 2.5 Infra: entity, jpa repo, adapter — `@DataJpaTest`
- [x] 2.6 REST DTOs + `LevelController` under `/api/v1/schools/{schoolId}/levels` — `@WebMvcTest`

## Phase 3: AcademicPeriod (overlap validation)

- [x] 3.1 Domain: `academic/period/model/AcademicPeriodId` VO, `PeriodType` enum, `AcademicPeriod` aggregate (create/restore/changeData/deactivate with date range), unit tests
- [x] 3.2 App port/in: `CreatePeriodUseCase`, `GetPeriodUseCase`, `ListPeriodsUseCase`, `UpdatePeriodUseCase`, `DeactivatePeriodUseCase`
- [x] 3.3 App port/out: `AcademicPeriodRepository` — findActiveByLevelId/findById/ findAllByLevelId/save
- [x] 3.4 App DTOs + `CreatePeriodService` (query active by levelId, overlap check: newStart < existing.end AND newEnd > existing.start) + `UpdatePeriodService` (re-validate on date change) + `DeactivatePeriodService` (check active eval periods) — Mockito tests of all overlap paths
- [x] 3.5 Infra: entity, jpa repo (findActiveByLevelId query), adapter — `@DataJpaTest`
- [x] 3.6 REST DTOs + `PeriodController` under `/api/v1/levels/{levelId}/periods` — `@WebMvcTest` with overlap 409 scenarios

## Phase 4: EvaluationPeriod (weight validation)

- [x] 4.1 Domain: `academic/evaluation/model/EvaluationPeriodId` VO, `EvaluationPeriod` aggregate (create/restore/changeName/changeWeight/changeDates/deactivate with weight), unit tests
- [x] 4.2 App port/in: `CreateEvaluationPeriodUseCase`, `GetEvaluationPeriodUseCase`, `ListEvaluationPeriodsByPeriodUseCase`, `UpdateEvaluationPeriodUseCase`, `DeactivateEvaluationPeriodUseCase`
- [x] 4.3 App port/out: `EvaluationPeriodRepository` — findById/findByPeriodId/sumWeightsByPeriodId/save
- [x] 4.4 App DTOs + `CreateEvaluationPeriodService` (weight sum ≤ 100 validation) + remaining services — Mockito tests
- [x] 4.5 Infra: entity, jpa repo, adapter — `@DataJpaTest`
- [x] 4.6 REST DTOs + `EvaluationPeriodController` under `/api/v1/periods/{periodId}/evaluations` — `@MockitoExtension` test

## Phase 5: Wiring

- [x] 5.1 Create `AcademicBeansConfig` — wire all evaluation use-case services + Clock
- [x] 5.2 Run `./gradlew build`, fix compilation issues, verify all tests pass
