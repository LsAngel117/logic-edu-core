# Evaluation Period Specification

## 1. Database Schema

**Requirement: Flyway V8**

The system MUST provide `evaluation_periods` via Flyway V8.

| Table | PK | Key Columns | Constraints |
|-------|----|-------------|-------------|
| `evaluation_periods` | id | period_id, name, sequence, weight, start_date, end_date, status | INDEX(period_id) |

- GIVEN blank PostgreSQL WHEN V8 runs THEN evaluation_periods table exists with schema

---

## 2. Create Evaluation Period

**Requirement: Create Evaluation Period**

POST /api/v1/periods/{periodId}/evaluation-periods MUST create a sub-period. Only allowed if parent structure has evaluationPeriodsPerPeriod > 0. Weights MUST sum to ≤ 100 within a period. Requires SCHOOL_ADMIN.

- GIVEN valid request with structure supporting evaluation periods and SCHOOL_ADMIN WHEN POST THEN 201
- GIVEN structure with evaluationPeriodsPerPeriod = 0 WHEN POST THEN 422
- GIVEN cumulative weight exceeds 100 in same period WHEN POST THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated request WHEN POST THEN 401

---

## 3. Get Evaluation Period

**Requirement: Get Evaluation Period by ID**

GET /api/v1/periods/{periodId}/evaluation-periods/{id} MUST return the evaluation period.

- GIVEN existing evaluation period WHEN GET THEN 200
- GIVEN unknown id WHEN GET THEN 404

---

## 4. List Evaluation Periods

**Requirement: List Evaluation Periods by Period**

GET /api/v1/periods/{periodId}/evaluation-periods MUST return all evaluation periods for the period.

- GIVEN period with evaluation periods WHEN GET THEN 200
- GIVEN period with no evaluation periods WHEN GET THEN 200 with empty list

---

## 5. Update Evaluation Period

**Requirement: Update Evaluation Period**

PUT /api/v1/periods/{periodId}/evaluation-periods/{id} MUST update name, weight, or dates. MUST re-validate weight sum.

- GIVEN active evaluation period and SCHOOL_ADMIN WHEN PUT valid data THEN 200
- GIVEN updated weight causes total > 100 WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 6. Deactivate Evaluation Period

**Requirement: Deactivate Evaluation Period**

PATCH /api/v1/periods/{periodId}/evaluation-periods/{id}/deactivate MUST set status=INACTIVE.

- GIVEN active evaluation period and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN already inactive evaluation period WHEN PATCH THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
