# Group Management Specification

## 1. Database Schema

**Requirement: Flyway V10**

The system MUST provide `groups` and `group_schedules` tables via Flyway V10.

| Table | Key Columns | Constraints |
|-------|-------------|-------------|
| `groups` | id, school_id (FK), subject_id (FK), academic_period_id (FK), branch_id (FK), teacher_id, code, capacity, status, version, created_at, updated_at | UNIQUE(school_id, code) |
| `group_schedules` | id, group_id (FK), day_of_week, start_time, end_time, classroom | FK → groups(id) CASCADE |

- GIVEN blank PostgreSQL WHEN V10 runs THEN groups and group_schedules tables exist with all columns and constraints

---

## 2. Create Group

**Requirement: Create Group**

POST /api/v1/schools/{schoolId}/groups MUST create a group with schedules. Requires SCHOOL_ADMIN. Validates subject/period/branch are active, teacher has TEACHER role, code unique per school, capacity > 0.

- GIVEN valid request with SCHOOL_ADMIN WHEN POST THEN 201 with GroupResponse including schedules
- GIVEN inactive subject WHEN POST THEN 422
- GIVEN inactive academic period WHEN POST THEN 422
- GIVEN inactive branch WHEN POST THEN 422
- GIVEN teacher without TEACHER role WHEN POST THEN 422
- GIVEN duplicate code in same school WHEN POST THEN 409
- GIVEN capacity <= 0 WHEN POST THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated WHEN POST THEN 401

---

## 3. Get Group

**Requirement: Get Group by ID**

GET /api/v1/schools/{schoolId}/groups/{id} MUST return the group with its schedules. Requires isAuthenticated.

- GIVEN existing group in same school WHEN GET THEN 200 with group and schedules
- GIVEN group belonging to different school WHEN GET THEN 404
- GIVEN unknown id WHEN GET THEN 404
- GIVEN unauthenticated WHEN GET THEN 401

---

## 4. List Groups

**Requirement: List Groups by School**

GET /api/v1/schools/{schoolId}/groups MUST return groups for the school. Optional filters: ?branchId=, ?periodId=. Requires isAuthenticated.

- GIVEN school with groups WHEN GET THEN 200 with list
- GIVEN school with no groups WHEN GET THEN 200 with empty list
- GIVEN ?branchId filter WHEN GET THEN 200 with filtered list
- GIVEN ?periodId filter WHEN GET THEN 200 with filtered list
- GIVEN unauthenticated WHEN GET THEN 401

---

## 5. Update Group

**Requirement: Update Group**

PUT /api/v1/schools/{schoolId}/groups/{id} MUST update group data. Requires SCHOOL_ADMIN. MUST reject update for INACTIVE groups. Re-validates all constraints.

- GIVEN ACTIVE group with SCHOOL_ADMIN WHEN PUT valid data THEN 200
- GIVEN INACTIVE group WHEN PUT THEN 422
- GIVEN duplicate code when updating WHEN PUT THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403
- GIVEN unauthenticated WHEN PUT THEN 401

---

## 6. Update Schedules

**Requirement: Update Group Schedules**

PUT /api/v1/schools/{schoolId}/groups/{id}/schedules MUST replace all schedules for the group. Requires SCHOOL_ADMIN.

- GIVEN ACTIVE group with SCHOOL_ADMIN WHEN PUT valid schedules THEN 200 with updated schedules
- GIVEN INACTIVE group WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 7. Deactivate Group

**Requirement: Deactivate Group**

PATCH /api/v1/schools/{schoolId}/groups/{id}/deactivate MUST set status to INACTIVE. Existing enrollments are preserved as historical records — NOT modified, NOT deleted. New enrollment attempts MUST return 422. Requires SCHOOL_ADMIN.

- GIVEN ACTIVE group with SCHOOL_ADMIN WHEN PATCH THEN 200 with status=INACTIVE
- GIVEN group with active enrollments WHEN PATCH THEN 200, enrollments preserved unchanged
- GIVEN already INACTIVE group WHEN PATCH THEN 200 (idempotent)
- GIVEN enrollment attempted on INACTIVE group WHEN POST /enrollments THEN 422 "Group is inactive"
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
