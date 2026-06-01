package com.logossystemsit.logiceducore.domain.academic.attendance.model;

import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class Attendance {

    private final AttendanceId id;
    private final GroupId groupId;
    private final UserId studentId;
    private final LocalDate date;
    private final AttendanceStatus status;
    private final String observations;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Attendance(
            AttendanceId id,
            GroupId groupId,
            UserId studentId,
            LocalDate date,
            AttendanceStatus status,
            String observations,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.groupId = Objects.requireNonNull(groupId, "groupId is required");
        this.studentId = Objects.requireNonNull(studentId, "studentId is required");
        this.date = Objects.requireNonNull(date, "date is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.observations = observations != null ? observations.trim() : null;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Attendance create(
            AttendanceId id,
            GroupId groupId,
            UserId studentId,
            LocalDate date,
            AttendanceStatus status,
            String observations,
            Instant now
    ) {
        return new Attendance(id, groupId, studentId, date, status, observations, now, now);
    }

    public static Attendance restore(
            AttendanceId id,
            GroupId groupId,
            UserId studentId,
            LocalDate date,
            AttendanceStatus status,
            String observations,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Attendance(id, groupId, studentId, date, status, observations, createdAt, updatedAt);
    }

    /* ---------- BEHAVIOR ---------- */

    public Attendance changeStatus(AttendanceStatus newStatus, String newObservations, Instant now) {
        Objects.requireNonNull(newStatus, "status is required");
        return new Attendance(
                this.id, this.groupId, this.studentId, this.date,
                newStatus, newObservations,
                this.createdAt, now
        );
    }

    /* ---------- EQUALS / HASHCODE ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ---------- GETTERS ---------- */

    public AttendanceId getId() { return id; }
    public GroupId getGroupId() { return groupId; }
    public UserId getStudentId() { return studentId; }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public String getObservations() { return observations; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
