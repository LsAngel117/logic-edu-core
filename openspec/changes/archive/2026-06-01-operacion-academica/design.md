# Design: Operación Académica — Group & Enrollment

## Technical Approach

Two new aggregates (Group, Enrollment) following the existing Subject/Branch pattern: immutable domain primaries with `create()`/`restore()` factories, hexagonal ports & adapters, JPA persistence with Flyway migrations. Group owns Schedule VOs via `@OneToMany(cascade=ALL, orphanRemoval=true)`. Enrollment enforces cross-aggregate rules at the application layer via port queries.

## Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Optimistic concurrency | `@Version Long version` on GroupEntity | Spring Data JPA auto-increments on save; version mismatch triggers `OptimisticLockException` mapped to 409. No distributed lock needed. |
| Enrollment capacity check | `countActiveByGroupId` + compare with `group.capacity` before save | Count reflects current DB state; Group.version guarantees no concurrent overfill. Unique `(user_id,group_id)` as safety net. |
| Cross-aggregate query | Custom `@Query` in `EnrollmentJpaRepository` | JOIN enrollment→group→subject filtering by userId+subjectId+periodId. Keeps logic in port, not in domain (clean boundaries). |
| Schedule as owned child | `@OneToMany(mappedBy="group", cascade=ALL, orphanRemoval=true)` | Schedule has no independent lifecycle. Full replace on update: delete all, insert new. |
| Deactivation policy | Soft: `status=INACTIVE`, no enrollment cascade | Preserves historical enrollments; new enrollments rejected at application layer (422). Idempotent: re-deactivating returns 200. |
| Exception mapping | Follow existing GlobalExceptionHandler | `IllegalArgumentException`→422, `IllegalStateException`→403, `NoSuchElementException`→404. OptimisticLockException→409 handled in controller catch. |

## Data Flow — EnrollStudent (critical path)

```
EnrollStudentCommand → EnrollStudentService
  1. groupRepo.findById(groupId)           → Group (with version)
  2. userRepo.findById(userId)              → validate student ACTIVE
  3. enrollmentRepo.existsByUserIdAndGroupId → 409 if duplicate
  4. enrollmentRepo.findActiveByUserIdAndSubjectAndPeriod → 422 if conflict
  5. enrollmentRepo.countActiveByGroupId     → check < group.capacity
  6. enrollmentRepo.save(Enrollment.create(…))
  7. groupRepo.save(group)                   → version check (optimistic lock)
      → if version mismatch: OptimisticLockException → 409
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/academic/group/model/Group.java` | Create | Aggregate: GroupId, SchoolId, SubjectId, AcademicPeriodId, BranchId, teacherId, code, capacity, status, version, timestamps, List\<Schedule\> |
| `domain/academic/group/model/GroupId.java` | Create | record(String value), `generate()` |
| `domain/academic/group/model/GroupStatus.java` | Create | enum ACTIVE, INACTIVE |
| `domain/academic/group/model/Schedule.java` | Create | record(DayOfWeek, LocalTime startTime, LocalTime endTime, String classroom); invariant startTime<endTime |
| `domain/academic/enrollment/model/Enrollment.java` | Create | Aggregate: EnrollmentId, UserId, GroupId, EnrollmentStatus, enrolledAt, updatedAt; behaviors: drop() |
| `domain/academic/enrollment/model/EnrollmentId.java` | Create | record(String value) |
| `domain/academic/enrollment/model/EnrollmentStatus.java` | Create | enum ACTIVE, INACTIVE, DROPPED |
| `application/academic/group/port/in/CreateGroupUseCase.java` | Create | Input port |
| `application/academic/group/port/in/GetGroupUseCase.java` | Create | Input port |
| `application/academic/group/port/in/ListGroupsUseCase.java` | Create | Input port |
| `application/academic/group/port/in/UpdateGroupUseCase.java` | Create | Input port |
| `application/academic/group/port/in/UpdateGroupSchedulesUseCase.java` | Create | Input port |
| `application/academic/group/port/in/DeactivateGroupUseCase.java` | Create | Input port |
| `application/academic/group/port/out/GroupRepository.java` | Create | save, findById, findBySchoolId, findBySchoolIdAndBranchId, findBySchoolIdAndPeriodId, existsBySchoolIdAndCode |
| `application/academic/group/usecase/CreateGroupService.java` | Create | 6-repo validation (school/branch/period/subject/teacher/code) |
| `application/academic/group/usecase/UpdateGroupService.java` | Create | Same validations, ensure ACTIVE |
| `application/academic/group/usecase/DeactivateGroupService.java` | Create | Set INACTIVE |
| `application/academic/group/usecase/GetGroupService.java` | Create | Find by ID |
| `application/academic/group/usecase/ListGroupsService.java` | Create | List with optional filters |
| `application/academic/group/usecase/UpdateGroupSchedulesService.java` | Create | Replace schedules |
| `application/academic/group/dto/command/CreateGroupCommand.java` | Create | record with ScheduleItem list |
| `application/academic/group/dto/command/UpdateGroupCommand.java` | Create | record |
| `application/academic/group/dto/result/GroupResult.java` | Create | record from(Group) |
| `application/academic/enrollment/port/in/EnrollStudentUseCase.java` | Create | Input port |
| `application/academic/enrollment/port/in/GetEnrollmentUseCase.java` | Create | Input port |
| `application/academic/enrollment/port/in/ListEnrollmentsByGroupUseCase.java` | Create | Input port |
| `application/academic/enrollment/port/in/DropEnrollmentUseCase.java` | Create | Input port |
| `application/academic/enrollment/port/out/EnrollmentRepository.java` | Create | save, findById, findByGroupId, countActiveByGroupId, existsByUserIdAndGroupId, findActiveByUserIdAndSubjectAndPeriod |
| `application/academic/enrollment/usecase/EnrollStudentService.java` | Create | Complex flow: validate group, student, duplicates, cross-aggregate, capacity, optimistic lock |
| `application/academic/enrollment/usecase/DropEnrollmentService.java` | Create | Set DROPPED |
| `application/academic/enrollment/usecase/GetEnrollmentService.java` | Create | Find by ID |
| `application/academic/enrollment/usecase/ListEnrollmentsByGroupService.java` | Create | List by group |
| `application/academic/enrollment/dto/command/EnrollStudentCommand.java` | Create | record |
| `application/academic/enrollment/dto/result/EnrollmentResult.java` | Create | record from(Enrollment) |
| `infrastructure/academic/group/persistence/entity/GroupEntity.java` | Create | @Entity, @Version Long version, @OneToMany schedules |
| `infrastructure/academic/group/persistence/entity/GroupScheduleEntity.java` | Create | @Entity, FK to groups |
| `infrastructure/academic/group/persistence/repository/GroupJpaRepository.java` | Create | Spring Data JPA |
| `infrastructure/academic/group/persistence/adapter/GroupRepositoryAdapter.java` | Create | mapToEntity/mapToDomain with schedules |
| `infrastructure/academic/enrollment/persistence/entity/EnrollmentEntity.java` | Create | @Entity |
| `infrastructure/academic/enrollment/persistence/repository/EnrollmentJpaRepository.java` | Create | Spring Data JPA + custom @Query |
| `infrastructure/academic/enrollment/persistence/adapter/EnrollmentRepositoryAdapter.java` | Create | mapToEntity/mapToDomain |
| `interfaces/rest/academic/group/controller/GroupController.java` | Create | /api/v1/schools/{schoolId}/groups |
| `interfaces/rest/academic/group/dto/request/CreateGroupRequest.java` | Create | DTO |
| `interfaces/rest/academic/group/dto/request/UpdateGroupRequest.java` | Create | DTO |
| `interfaces/rest/academic/group/dto/request/UpdateSchedulesRequest.java` | Create | DTO |
| `interfaces/rest/academic/group/dto/response/GroupResponse.java` | Create | DTO |
| `interfaces/rest/academic/enrollment/controller/EnrollmentController.java` | Create | /api/v1/enrollments, groups/{groupId}/enrollments |
| `interfaces/rest/academic/enrollment/dto/request/EnrollStudentRequest.java` | Create | DTO |
| `interfaces/rest/academic/enrollment/dto/response/EnrollmentResponse.java` | Create | DTO |
| `resources/db/migration/V10__create_groups.sql` | Create | groups + group_schedules tables |
| `resources/db/migration/V11__create_enrollments.sql` | Create | enrollments table |
| `infrastructure/config/AcademicBeansConfig.java` | Modify | Add Group + Enrollment service beans |
| `infrastructure/config/PersistenceConfig.java` | Modify | Add Group + Enrollment adapter beans |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Domain | Group.create, Schedule validation, Enrollment.drop | JUnit 5 unit tests |
| Domain | Group.deactivate idempotency | Unit |
| Application | CreateGroupService validations | Mock repositories |
| Application | EnrollStudentService concurrency | Integration test with 2 concurrent threads, verify one gets 409 |
| Application | Cross-aggregate duplicate rule | Mock EnrollmentRepository query |
| Infrastructure | GroupRepositoryAdapter mapToEntity/mapToDomain | Unit test mapping |
| Infrastructure | EnrollmentJpaRepository custom @Query | `@DataJpaTest` |
| REST | Controller endpoints | `@WebMvcTest` with mocked use cases |

## Open Questions

- [ ] Should `EnrollmentRepository.findActiveByUserIdAndSubjectAndPeriod` return just a boolean or the list of conflicting enrollments? (Spec says 422, boolean suffices)
