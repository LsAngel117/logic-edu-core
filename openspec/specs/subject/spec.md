# Subject Specification

## 1. Database Schema

**Requirement: Flyway V9**

The system MUST provide `subjects` table via Flyway V9.

| Table | PK | Key Columns | Constraints |
|-------|----|-------------|-------------|
| `subjects` | id | school_id, code, name, description, hours, status | UNIQUE(school_id, code) |

- GIVEN blank PostgreSQL WHEN V9 runs THEN subjects table exists with schema and unique index

---

## 2. Create Subject

**Requirement: Create Subject**

POST /api/v1/schools/{schoolId}/subjects MUST create a subject with unique code within school. Requires SCHOOL_ADMIN. Hours MUST be >= 0.

- GIVEN valid request with SCHOOL_ADMIN WHEN POST THEN 201 with SubjectResponse
- GIVEN duplicate code within same school WHEN POST THEN 409
- GIVEN school not found WHEN POST THEN 404
- GIVEN hours < 0 WHEN POST THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated request WHEN POST THEN 401

---

## 3. Get Subject

**Requirement: Get Subject by ID**

GET /api/v1/schools/{schoolId}/subjects/{id} MUST return the subject scoped to the school.

- GIVEN existing subject in same school WHEN GET THEN 200
- GIVEN subject belonging to different school WHEN GET THEN 404
- GIVEN unknown id WHEN GET THEN 404
- GIVEN unauthenticated request WHEN GET THEN 401

---

## 4. List Subjects

**Requirement: List Subjects by School**

GET /api/v1/schools/{schoolId}/subjects MUST return all subjects for the school.

- GIVEN school with subjects WHEN GET THEN 200 with list
- GIVEN school with no subjects WHEN GET THEN 200 with empty list
- GIVEN unauthenticated request WHEN GET THEN 401

---

## 5. Update Subject

**Requirement: Update Subject Data**

PUT /api/v1/schools/{schoolId}/subjects/{id} MUST update name, description, or hours when no constraints violated. Requires SCHOOL_ADMIN. MUST reject update for INACTIVE subjects.

- GIVEN ACTIVE subject with SCHOOL_ADMIN WHEN PUT valid data THEN 200
- GIVEN INACTIVE subject WHEN PUT THEN 422
- GIVEN duplicate code when updating WHEN PUT THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403
- GIVEN unauthenticated request WHEN PUT THEN 401

---

## 6. Deactivate Subject

**Requirement: Deactivate Subject**

PATCH /api/v1/schools/{schoolId}/subjects/{id}/deactivate MUST set status to INACTIVE. Requires SCHOOL_ADMIN. Idempotent for already INACTIVE subjects.

- GIVEN ACTIVE subject with SCHOOL_ADMIN WHEN PATCH THEN 200 with status=INACTIVE
- GIVEN already INACTIVE subject WHEN PATCH THEN 200 (idempotent)
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
- GIVEN unauthenticated request WHEN PATCH THEN 401
