# Delta: Seguimiento Académico — Attendance, Assessment, Grade

---

## 1. Attendance

Track per group/date with status PRESENT/ABSENT/LATE/EXCUSED.

### Requirement: Flyway V12
`attendances` MUST have UK(group_id, date, student_id).

| Table | PK | Constraints |
|-------|----|-------------|
| attendances | id | UK(group_id, date, student_id) |

- GIVEN blank DB WHEN V12 runs THEN table exists

### Requirement: Register Attendance
POST /api/v1/groups/{groupId}/attendances → 201. Date MUST be within group's active period. TEACHER or SCHOOL_ADMIN.

- GIVEN valid request inside active period WHEN POST THEN 201
- GIVEN duplicate UK WHEN POST THEN 409
- GIVEN date outside active period WHEN POST THEN 422
- GIVEN non-TEACHER/SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated WHEN POST THEN 401

### Requirement: Get by Date
GET /api/v1/groups/{groupId}/attendances/{date} → 200.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

### Requirement: List by Group
GET /api/v1/groups/{groupId}/attendances → 200.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

### Requirement: Update
PUT /api/v1/groups/{groupId}/attendances/{date}/{studentId} → 200. Owner TEACHER only.

- GIVEN existing record and owner TEACHER WHEN PUT THEN 200
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown record WHEN PUT THEN 404

---

## 2. Assessment

Per group, optionally linked to evaluation periods.

### Requirement: Flyway V13
`assessments` MUST have UK(group_id, name), optional FK to evaluation_periods.

| Table | PK | Constraints |
|-------|----|-------------|
| assessments | id | UK(group_id, name), FK(evaluation_periods) nullable |

- GIVEN blank DB WHEN V13 runs THEN table exists

### Requirement: Create
POST /api/v1/groups/{groupId}/assessments → 201. weight > 0, maxScore > 0. Group TEACHER.

- GIVEN valid data and owner TEACHER WHEN POST THEN 201
- GIVEN weight ≤ 0 WHEN POST THEN 422
- GIVEN maxScore ≤ 0 WHEN POST THEN 422
- GIVEN duplicate name in group WHEN POST THEN 409
- GIVEN non-owner TEACHER WHEN POST THEN 403

### Requirement: Get
GET /api/v1/groups/{groupId}/assessments/{id} → 200.

- GIVEN exists WHEN GET THEN 200
- GIVEN unknown WHEN GET THEN 404

### Requirement: List by Group
GET /api/v1/groups/{groupId}/assessments → 200.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

### Requirement: Update
PUT /api/v1/groups/{groupId}/assessments/{id} → 200. Group TEACHER.

- GIVEN exists and owner TEACHER WHEN PUT THEN 200
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown WHEN PUT THEN 404

### Requirement: Delete
DELETE /api/v1/groups/{groupId}/assessments/{id} → 204. MUST NOT delete if grades exist. Group TEACHER.

- GIVEN no grades and owner TEACHER WHEN DELETE THEN 204
- GIVEN existing grades WHEN DELETE THEN 409
- GIVEN non-owner TEACHER WHEN DELETE THEN 403
- GIVEN unknown WHEN DELETE THEN 404

---

## 3. Grade

Per assessment with cross-aggregate teacher validation.

### Requirement: Flyway V14
`grades` MUST have UK(assessment_id, student_id).

| Table | PK | Constraints |
|-------|----|-------------|
| grades | id | UK(assessment_id, student_id), FK(assessments.id) |

- GIVEN blank DB WHEN V14 runs THEN table exists

### Requirement: Register
POST /api/v1/assessments/{assessmentId}/grades → 201. value ≤ assessment.maxScore. Auth: Grade→Assessment→Group→teacherId. Group TEACHER.

- GIVEN valid grade and owner TEACHER WHEN POST THEN 201
- GIVEN value > assessment.maxScore WHEN POST THEN 422
- GIVEN duplicate UK WHEN POST THEN 409
- GIVEN non-owner TEACHER WHEN POST THEN 403
- GIVEN unknown assessment WHEN POST THEN 404

### Requirement: Get
GET /api/v1/assessments/{assessmentId}/grades/{id} → 200.

- GIVEN exists WHEN GET THEN 200
- GIVEN unknown WHEN GET THEN 404

### Requirement: List by Assessment
GET /api/v1/assessments/{assessmentId}/grades → 200.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

### Requirement: Update
PUT /api/v1/assessments/{assessmentId}/grades/{studentId} → 200. value ≤ assessment.maxScore. Group TEACHER.

- GIVEN exists and owner TEACHER WHEN PUT THEN 200
- GIVEN value > maxScore WHEN PUT THEN 422
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown WHEN PUT THEN 404
