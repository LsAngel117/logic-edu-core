# Design: Seguimiento Académico — Attendance, Assessment, Grade

## Technical Approach

Three aggregate roots (Attendance, Assessment, Grade) following existing hexagonal patterns: immutable `final` domain models with `create()`/`restore()` factories, separate use-case services per operation, JPA adapters mapped by hand, and REST controllers routing exceptions to `ResponseStatusException`. Teacher authorization is applied at the application layer by injecting `GroupRepository` and comparing `Group.teacherId` against the authenticated `userId` passed from controllers.

## Architecture Decisions

| # | Decision | Option | Tradeoff | Verdict |
|---|----------|--------|----------|---------|
| 1 | **Teacher auth location** | Application service vs. Controller/annotation | Service keeps auth testable without Spring Security mocks | Application service |
| 2 | **Attendance date validity** | Enforce `date ∈ [group startDate, endDate]` in service | Requires `GroupRepository.findById`; rejects out-of-range before DB hits | Validate in RegisterAttendanceService |
| 3 | **Assessment delete guard** | Count grades before delete vs. DB FK `ON DELETE RESTRICT` | Service check gives explicit 409; FK gives 500 with unclear message | Service check → 409 |
| 4 | **Grade cross-aggregate auth** | Single query: `groupRepository.findByAssessmentId()` vs. chained loads | Single query avoids N+1; repository method `findGroupByAssessmentId` returns `Group` directly | Single optimized query |
| 5 | **Grade value ≤ maxScore** | Validate in `RegisterGradeService` / `UpdateGradeService` | Load Assessment, compare `value > maxScore` → 422 | Service validation |
| 6 | **UK enforcement** | DB unique index + service pre-check | Index catches races; optional pre-check gives better error for 409 | DB index primary, service optional |

## Data Flow

### Attendance (standard group-scoped write)

```
Controller ──(userId, request)──→ RegisterAttendanceService
    │                                  │
    │                           GroupRepository.findById(groupId)
    │                                  │
    │                           group.teacherId == userId? ──→ NO: 403
    │                                  │ YES
    │                           Attendance.create(id, groupId, date, studentId, status, now)
    │                                  │
    │                           attendanceRepository.save(attendance)
    │                                  │
    ◄────── 201 + AttendanceResponse ──┘
```

### Grade (cross-aggregate auth)

```
Controller ──(userId, request)──→ RegisterGradeService
    │                                  │
    │                    assessmentRepository.findById(assessmentId)
    │                                  │
    │                    groupRepository.findByAssessmentId(assessmentId)
    │                                  │
    │                    group.teacherId == userId? ──→ NO: 403
    │                                  │ YES
    │                    Grade.create(id, assessmentId, studentId, value, now)
    │                                  │
    │                    gradeRepository.save(grade)
    │                                  │
    ◄────── 201 + GradeResponse ───────┘
```

### Assessment Delete Guard

```
Controller ──→ DeleteAssessmentService
                    │
         groupRepository.findById(groupId) → teacher auth
         gradeRepository.countByAssessmentId(assessmentId) > 0? ──→ YES: 409
                    │ NO
         assessmentRepository.delete(assessment) ──→ 204
```

## File Changes

### Attendance (3 domain + 8 app + 3 infra + 3 REST = 17 source files)
| File | Action | Description |
|------|--------|-------------|
| `domain/academic/attendance/model/Attendance.java` | Create | Immutable final class, create/restore, changeStatus behavior |
| `domain/academic/attendance/model/AttendanceId.java` | Create | Record(value), UUID.generate() |
| `domain/academic/attendance/model/AttendanceStatus.java` | Create | Enum: PRESENT, ABSENT, LATE, EXCUSED |
| `application/academic/attendance/port/out/AttendanceRepository.java` | Create | Port: save, findById, findByGroupIdAndDate, findByGroupId, findByGroupIdAndDateAndStudentId |
| `application/academic/attendance/port/in/RegisterAttendanceUseCase.java` | Create | Port IN |
| `application/academic/attendance/port/in/GetAttendanceByDateUseCase.java` | Create | Port IN |
| `application/academic/attendance/port/in/ListAttendancesByGroupUseCase.java` | Create | Port IN |
| `application/academic/attendance/port/in/UpdateAttendanceUseCase.java` | Create | Port IN |
| `application/academic/attendance/usecase/RegisterAttendanceService.java` | Create | Injects GroupRepository + AttendanceRepository; teacher auth + date-range check |
| `application/academic/attendance/usecase/GetAttendanceByDateService.java` | Create | Read-only |
| `application/academic/attendance/usecase/ListAttendancesByGroupService.java` | Create | Read-only |
| `application/academic/attendance/usecase/UpdateAttendanceService.java` | Create | Teacher auth check |
| `application/academic/attendance/dto/command/RegisterAttendanceCommand.java` | Create | Record: GroupId, LocalDate, UserId(studentId), AttendanceStatus |
| `application/academic/attendance/dto/command/UpdateAttendanceCommand.java` | Create | Record: AttendanceId, AttendanceStatus |
| `application/academic/attendance/dto/result/AttendanceResult.java` | Create | Record with static from(Attendance) |
| `infrastructure/academic/attendance/persistence/entity/AttendanceEntity.java` | Create | JPA @Entity, @Version, getters+setters |
| `infrastructure/academic/attendance/persistence/repository/AttendanceJpaRepository.java` | Create | JpaRepository + custom query methods |
| `infrastructure/academic/attendance/persistence/adapter/AttendanceRepositoryAdapter.java` | Create | Manual mapToEntity/mapToDomain |
| `interfaces/rest/academic/attendance/controller/AttendanceController.java` | Create | @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')") for writes |
| `interfaces/rest/academic/attendance/dto/request/RegisterAttendanceRequest.java` | Create | Record with @JsonProperty |
| `interfaces/rest/academic/attendance/dto/request/UpdateAttendanceRequest.java` | Create | Record with @JsonProperty |
| `interfaces/rest/academic/attendance/dto/response/AttendanceResponse.java` | Create | Record with from(Result) |

### Assessment (17 source files)
Same structure as Attendance, plus:
- `FindGroupByAssessmentId` method on `GroupRepository` port
- `weight` (BigDecimal > 0) and `maxScore` (BigDecimal > 0) validation in domain
- Optional `evaluationPeriodId` FK (nullable `EvaluationPeriodId` field)
- Delete endpoint with `countByAssessmentId` guard in `DeleteAssessmentService`

### Grade (14 source files — simpler, no in-group listing)
- `application/academic/grade/usecase/RegisterGradeService.java` — cross-aggregate: loads Assessment → Group → teacherId check
- `application/academic/grade/usecase/UpdateGradeService.java` — same cross-aggregate + maxScore check
- `application/academic/grade/port/out/GradeRepository.java` — includes `countByAssessmentId`
- No native list-by-group; list is by assessment only

### Modified Files
| File | Change | Reason |
|------|--------|--------|
| `infrastructure/config/AcademicBeansConfig.java` | Add ~13 bean definitions | Wire attendance/assessment/grade services |
| `infrastructure/config/PersistenceConfig.java` | Add 3 adapter beans | Wire JPA adapters |
| `application/academic/group/port/out/GroupRepository.java` | Add `findGroupByAssessmentId` | Grade cross-aggregate auth |
| `resources/db/migration/` | Create V12, V13, V14 | Tables + unique indexes |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Domain | Id/Status validation, create/restore, behavior methods | JUnit 5 + AssertJ |
| Application | Teacher auth rejection, date range, maxScore, duplicate UK, delete guard | Mockito mocks for all ports, verify `save()` calls |
| Infra JPA | Custom query methods, unique index violation | `@DataJpaTest` with embedded DB |
| Infra Adapter | MapToEntity/MapToDomain roundtrip | Unit test with real entity instances |
| REST | Endpoint routing, error mapping, auth annotations | `@WebMvcTest` + `@WithMockUser` |

## Migration / Rollout

Flyway V12 (attendances), V13 (assessments), V14 (grades). No data migration required — new tables only.

## Open Questions

None.
