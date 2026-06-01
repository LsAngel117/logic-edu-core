# Attendance Management Specification

## 1. Database Schema

**Requirement: Flyway V12**

The system MUST provide an `attendances` table via Flyway V12.

| Table | PK | Constraints |
|-------|----|-------------|
| attendances | id | UK(group_id, date, student_id) |

- GIVEN blank DB WHEN V12 runs THEN table exists

---

## 2. Register Attendance

**Requirement: Register Attendance**

POST /api/v1/groups/{groupId}/attendances MUST create an attendance record. Date MUST be within group's active period. Requires TEACHER or SCHOOL_ADMIN role.

- GIVEN valid request inside active period WHEN POST THEN 201
- GIVEN duplicate UK WHEN POST THEN 409
- GIVEN date outside active period WHEN POST THEN 422
- GIVEN non-TEACHER/SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated WHEN POST THEN 401

---

## 3. Get Attendance by Date

**Requirement: Get by Date**

GET /api/v1/groups/{groupId}/attendances/{date} MUST return attendance records for the given date.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

---

## 4. List Attendances by Group

**Requirement: List by Group**

GET /api/v1/groups/{groupId}/attendances MUST return all attendance records for the group.

- GIVEN records exist WHEN GET THEN 200
- GIVEN no records WHEN GET THEN 200 empty list

---

## 5. Update Attendance

**Requirement: Update**

PUT /api/v1/groups/{groupId}/attendances/{date}/{studentId} MUST update an attendance record. Owner TEACHER only.

- GIVEN existing record and owner TEACHER WHEN PUT THEN 200
- GIVEN non-owner TEACHER WHEN PUT THEN 403
- GIVEN unknown record WHEN PUT THEN 404
