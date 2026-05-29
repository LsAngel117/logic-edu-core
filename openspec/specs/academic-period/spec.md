# Academic Period Specification

## 1. Database Schema

**Requirement: Flyway V7**

The system MUST provide `academic_periods` via Flyway V7.

| Table | PK | Key Columns | Constraints |
|-------|----|-------------|-------------|
| `academic_periods` | id | level_id, period_type, name, sequence, start_date, end_date, status | INDEX(level_id, start_date, end_date) |

- GIVEN blank PostgreSQL WHEN V7 runs THEN academic_periods table exists with schema and index

---

## 2. Create Period

**Requirement: Create Academic Period**

POST /api/v1/levels/{levelId}/periods MUST create a period with non-overlapping date range within same level. Requires SCHOOL_ADMIN.

- GIVEN valid request with non-overlapping dates and SCHOOL_ADMIN WHEN POST THEN 201
- GIVEN overlapping dates in same level WHEN POST THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated request WHEN POST THEN 401

---

## 3. Get Period

**Requirement: Get Period by ID**

GET /api/v1/levels/{levelId}/periods/{id} MUST return the period.

- GIVEN existing period WHEN GET THEN 200
- GIVEN unknown id WHEN GET THEN 404

---

## 4. List Periods

**Requirement: List Periods by Level**

GET /api/v1/levels/{levelId}/periods MUST return all periods for the level.

- GIVEN level with periods WHEN GET THEN 200 with period list
- GIVEN level with no periods WHEN GET THEN 200 with empty list

---

## 5. Update Period

**Requirement: Update Period Data**

PUT /api/v1/levels/{levelId}/periods/{id} MUST update name, dates, or sequence. MUST re-validate overlap on date change.

- GIVEN active period and SCHOOL_ADMIN WHEN PUT valid data THEN 200
- GIVEN overlapping dates after update WHEN PUT THEN 409
- GIVEN INACTIVE period WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 6. Deactivate Period

**Requirement: Deactivate Period**

PATCH /api/v1/levels/{levelId}/periods/{id}/deactivate MUST set status=INACTIVE. MUST reject if period has active evaluation periods or groups.

- GIVEN period with no active evaluation periods or groups and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN period with active evaluation periods WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
