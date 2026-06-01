# Assessment Management Specification

## 1. Database Schema

**Requirement: Flyway V13**

The system MUST provide an `assessments` table via Flyway V13 with optional FK to evaluation_periods.

| Table | PK | Constraints |
|-------|----|-------------|
| assessments | id | UK(group_id, name), FK(evaluation_periods) nullable |

- GIVEN blank DB WHEN V13 runs THEN table exists

---

## 2. Create Assessment

**Requirement: Create**

POST /api/v1/groups/{groupId}/assessments MUST create an assessment. weight > 0, maxScore > 0. Group TEACHER only.

- GIVEN valid data and owner TEACHER WHEN POST THEN 201
- GIVEN weight ≤ 0 WHEN POST THEN 422
- GIVEN maxScore ≤ 0 WHEN POST THEN 422
- GIVEN duplicate name in group WHEN POST THEN 409
- GIVEN non-owner TEACHER WHEN POST THEN 403

---

## 3. Get Assessment

**Requirement: Get**

GET /api/v1/groups/{groupId}/assessments/{id} MUST return an assessment by ID.

- GIVEN exists WHEN GET THEN 200
- GIVEN unknown WHEN GET THEN 404

---

## 4. List Assessments by Group

**Requirement: List by Group**

GET /api/v1/groups/{groupId}/assessments MUST return all assessments for a group.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

---

## 5. Update Assessment

**Requirement: Update**

PUT /api/v1/groups/{groupId}/assessments/{id} MUST update an assessment. Group TEACHER only.

- GIVEN exists and owner TEACHER WHEN PUT THEN 200
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown WHEN PUT THEN 404

---

## 6. Delete Assessment

**Requirement: Delete**

DELETE /api/v1/groups/{groupId}/assessments/{id} MUST delete an assessment. MUST NOT delete if grades exist. Group TEACHER only.

- GIVEN no grades and owner TEACHER WHEN DELETE THEN 204
- GIVEN existing grades WHEN DELETE THEN 409
- GIVEN non-owner TEACHER WHEN DELETE THEN 403
- GIVEN unknown WHEN DELETE THEN 404
