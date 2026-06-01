# Enrollment Management Specification

## 1. Database Schema

**Requirement: Flyway V11**

The system MUST provide `enrollments` table via Flyway V11.

| Table | Key Columns | Constraints |
|-------|-------------|-------------|
| `enrollments` | id, user_id (FK), group_id (FK), status, enrolled_at, updated_at | UNIQUE(user_id, group_id) |

- GIVEN blank PostgreSQL WHEN V11 runs THEN enrollments table exists with all columns and unique constraint

---

## 2. Enroll Student

**Requirement: Create Enrollment**

POST /api/v1/enrollments MUST create an enrollment with cross-aggregate validation. Requires SCHOOL_ADMIN. Validates: no duplicate (user+group), no same subject+period for same student, group active, student active, capacity available via optimistic locking (Group.version). On version mismatch → 409.

- GIVEN valid request with SCHOOL_ADMIN WHEN POST THEN 201 with EnrollmentResponse
- GIVEN duplicate user+group enrollment WHEN POST THEN 409 (unique constraint or service check)
- GIVEN student already enrolled in another group with same subject+period WHEN POST THEN 422
- GIVEN inactive group WHEN POST THEN 422 "Group is inactive"
- GIVEN inactive student WHEN POST THEN 422
- GIVEN group at capacity WHEN POST THEN 409 "Group capacity changed, please retry"
- GIVEN concurrent enrollment causes version mismatch WHEN POST THEN 409 "Group capacity changed, please retry"
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated WHEN POST THEN 401

---

## 3. Get Enrollment

**Requirement: Get Enrollment by ID**

GET /api/v1/enrollments/{id} MUST return enrollment details. Requires isAuthenticated.

- GIVEN existing enrollment WHEN GET THEN 200
- GIVEN unknown id WHEN GET THEN 404
- GIVEN unauthenticated WHEN GET THEN 401

---

## 4. List Enrollments by Group

**Requirement: List Enrollments by Group**

GET /api/v1/groups/{groupId}/enrollments MUST return all enrollments for a group. Requires isAuthenticated.

- GIVEN group with enrollments WHEN GET THEN 200 with list
- GIVEN group with no enrollments WHEN GET THEN 200 with empty list
- GIVEN unknown groupId WHEN GET THEN 404
- GIVEN unauthenticated WHEN GET THEN 401

---

## 5. Drop Enrollment

**Requirement: Drop Enrollment**

PATCH /api/v1/enrollments/{id}/drop MUST set status to DROPPED. Requires SCHOOL_ADMIN.

- GIVEN ACTIVE enrollment with SCHOOL_ADMIN WHEN PATCH THEN 200 with status=DROPPED
- GIVEN already DROPPED enrollment WHEN PATCH THEN 200 (idempotent)
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
- GIVEN unauthenticated WHEN PATCH THEN 401
