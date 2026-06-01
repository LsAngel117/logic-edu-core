# Tasks: Seguimiento Académico — Attendance, Assessment, Grade

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~2500–3500 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR1: V12-V14 + Attendance → PR2: Assessment → PR3: Grade |
| Delivery strategy | ask-always |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: pending
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Base | Notes |
|------|------|-----------|------|-------|
| 1 | V12-V14 + Attendance | PR #1 | main | Migrations + full Attendance stack (~800 lines) |
| 2 | Assessment | PR #2 | main | Full Assessment stack (~900 lines) |
| 3 | Grade | PR #3 | main | Full Grade stack with cross-aggregate auth (~1000 lines) |

## Phase 1: Foundation — Flyway V12–V14

- [x] 1.1 Create `V12__create_attendances.sql` — id, group_id, date, student_id, status, created_at, updated_at. UNIQUE(group_id, date, student_id)
- [x] 1.2 Create `V13__create_assessments.sql` — id, group_id, name, description, weight, max_score, evaluation_period_id (nullable FK), created_at, updated_at. UNIQUE(group_id, name), FK(evaluation_periods.id)
- [x] 1.3 Create `V14__create_grades.sql` — id, assessment_id, student_id, value, created_at, updated_at. UNIQUE(assessment_id, student_id), FK(assessments.id)

## Phase 2: Attendance — Domain + App + Infra + REST + Tests

- [x] 2.1 Domain: `AttendanceStatus` enum (PRESENT/ABSENT/LATE/EXCUSED), `AttendanceId` VO, `Attendance` aggregate (create/restore/changeStatus), unit tests
- [x] 2.2 App port/in: `RegisterAttendanceUseCase`, `GetAttendanceByDateUseCase`, `ListAttendancesByGroupUseCase`, `UpdateAttendanceUseCase`
- [x] 2.3 App port/out: `AttendanceRepository` — save/findById/findByGroupIdAndDate/findByGroupId/findByGroupIdAndDateAndStudentId
- [x] 2.4 App DTOs: `RegisterAttendanceCommand`, `UpdateAttendanceCommand`, `AttendanceResult`
- [x] 2.5 App services: `RegisterAttendanceService` (teacher auth + date-range check), `GetAttendanceByDateService`, `ListAttendancesByGroupService`, `UpdateAttendanceService` (owner teacher check) — Mockito tests
- [x] 2.6 Infra: `AttendanceEntity`, `AttendanceJpaRepository`, `AttendanceRepositoryAdapter` — `@DataJpaTest` + map roundtrip test
- [x] 2.7 REST DTOs: `RegisterAttendanceRequest`, `UpdateAttendanceRequest`, `AttendanceResponse`
- [x] 2.8 REST: `AttendanceController` under `/api/v1/groups/{groupId}/attendances` — `@WebMvcTest` with all spec scenarios

## Phase 3: Assessment — Domain + App + Infra + REST + Tests

- [ ] 3.1 Domain: `AssessmentId` VO, `Assessment` aggregate (create/restore/changeData) with weight>0 and maxScore>0 validation, unit tests
- [ ] 3.2 App port/in: `CreateAssessmentUseCase`, `GetAssessmentUseCase`, `ListAssessmentsUseCase`, `UpdateAssessmentUseCase`, `DeleteAssessmentUseCase`
- [ ] 3.3 App port/out: `AssessmentRepository` — save/findById/findByGroupId/existsByNameInGroup
- [ ] 3.4 App DTOs: `CreateAssessmentCommand`, `UpdateAssessmentCommand`, `AssessmentResult`
- [ ] 3.5 App services: `CreateAssessmentService`, `GetAssessmentService`, `ListAssessmentsService`, `UpdateAssessmentService`, `DeleteAssessmentService` (grade count guard → 409) — Mockito tests
- [ ] 3.6 Infra: `AssessmentEntity`, `AssessmentJpaRepository`, `AssessmentRepositoryAdapter` — `@DataJpaTest` + map roundtrip
- [ ] 3.7 REST DTOs: `CreateAssessmentRequest`, `UpdateAssessmentRequest`, `AssessmentResponse`
- [ ] 3.8 REST: `AssessmentController` under `/api/v1/groups/{groupId}/assessments` — `@WebMvcTest` covering delete-with-grades 409

## Phase 4: Grade — Domain + App + Infra + REST + Tests

- [ ] 4.1 Domain: `GradeId` VO, `Grade` aggregate (create/restore), unit tests
- [ ] 4.2 App port/in: `RegisterGradeUseCase`, `GetGradeUseCase`, `ListGradesByAssessmentUseCase`, `UpdateGradeUseCase`
- [ ] 4.3 App port/out: `GradeRepository` — save/findById/findByAssessmentId/findByAssessmentIdAndStudentId/countByAssessmentId
- [ ] 4.4 App DTOs: `RegisterGradeCommand`, `UpdateGradeCommand`, `GradeResult`
- [ ] 4.5 App services: `RegisterGradeService` (cross-aggregate: Assessment→Group→teacherId + maxScore check), `GetGradeService`, `ListGradesByAssessmentService`, `UpdateGradeService` (same auth chain) — Mockito tests
- [ ] 4.6 Infra: `GradeEntity`, `GradeJpaRepository`, `GradeRepositoryAdapter` — `@DataJpaTest`
- [ ] 4.7 REST DTOs: `RegisterGradeRequest`, `UpdateGradeRequest`, `GradeResponse`
- [ ] 4.8 REST: `GradeController` under `/api/v1/assessments/{assessmentId}/grades` — `@WebMvcTest` covering value>maxScore 422 and cross-aggregate auth 403

## Phase 5: Wiring

- [ ] 5.1 Add `findGroupByAssessmentId(Long assessmentId)` to `GroupRepository` port
- [ ] 5.2 Wire all 13 service beans in `AcademicBeansConfig` + 3 adapter beans in `PersistenceConfig`
- [ ] 5.3 Run `./gradlew build`, fix compilation issues, verify all tests pass
