# Grade Management Specification

## 1. Database Schema

**Requirement: Flyway V14**

The system MUST provide a `grades` table via Flyway V14.

| Table | PK | Constraints |
|-------|----|-------------|
| grades | id | UK(assessment_id, student_id), FK(assessments.id) |

- GIVEN blank DB WHEN V14 runs THEN table exists

---

## 2. Register Grade

**Requirement: Register**

POST /api/v1/assessments/{assessmentId}/grades MUST register a grade. value ≤ assessment.maxScore. Auth chain: Grade → Assessment → Group → teacherId. Group TEACHER only.

- GIVEN valid grade and owner TEACHER WHEN POST THEN 201
- GIVEN value > assessment.maxScore WHEN POST THEN 422
- GIVEN duplicate UK WHEN POST THEN 409
- GIVEN non-owner TEACHER WHEN POST THEN 403
- GIVEN unknown assessment WHEN POST THEN 404

---

## 3. Get Grade

**Requirement: Get**

GET /api/v1/assessments/{assessmentId}/grades/{id} MUST return a grade by ID.

- GIVEN exists WHEN GET THEN 200
- GIVEN unknown WHEN GET THEN 404

---

## 4. List Grades by Assessment

**Requirement: List by Assessment**

GET /api/v1/assessments/{assessmentId}/grades MUST return all grades for an assessment.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

---

## 5. Update Grade

**Requirement: Update**

PUT /api/v1/assessments/{assessmentId}/grades/{studentId} MUST update a grade. value ≤ assessment.maxScore. Group TEACHER only.

- GIVEN exists and owner TEACHER WHEN PUT THEN 200
- GIVEN value > maxScore WHEN PUT THEN 422
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown WHEN PUT THEN 404
