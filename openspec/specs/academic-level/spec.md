# Academic Level Specification

## 1. Database Schema

**Requirement: Flyway V6**

The system MUST provide `academic_levels` via Flyway V6.

| Table | PK | Key Columns | Constraints |
|-------|----|-------------|-------------|
| `academic_levels` | id | school_id, name, number, status | UNIQUE(school_id, number) |

- GIVEN blank PostgreSQL WHEN V6 runs THEN academic_levels table exists with schema and unique index

---

## 2. Create Level

**Requirement: Create Academic Level**

POST /api/v1/schools/{schoolId}/levels MUST create a level with unique number within school. Requires SCHOOL_ADMIN.

- GIVEN valid request with SCHOOL_ADMIN WHEN POST THEN 201 with LevelResponse
- GIVEN duplicate number within same school WHEN POST THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated request WHEN POST THEN 401

---

## 3. Get Level

**Requirement: Get Level by ID**

GET /api/v1/schools/{schoolId}/levels/{id} MUST return the level scoped to the school.

- GIVEN existing level in same school WHEN GET THEN 200
- GIVEN level belonging to different school WHEN GET THEN 403
- GIVEN unknown id THEN 404

---

## 4. List Levels

**Requirement: List Levels by School**

GET /api/v1/schools/{schoolId}/levels MUST return all levels for the school.

- GIVEN school with levels WHEN GET THEN 200 with list
- GIVEN school with no levels WHEN GET THEN 200 with empty list

---

## 5. Update Level

**Requirement: Update Level Data**

PUT /api/v1/schools/{schoolId}/levels/{id} MUST update name or number if no conflict.

- GIVEN active level and SCHOOL_ADMIN WHEN PUT valid data THEN 200
- GIVEN duplicate number when updating WHEN PUT THEN 409
- GIVEN INACTIVE level WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 6. Deactivate Level

**Requirement: Deactivate Level**

PATCH /api/v1/schools/{schoolId}/levels/{id}/deactivate MUST set status=INACTIVE. MUST reject if level has active periods.

- GIVEN level with no active periods and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN level with active periods WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
