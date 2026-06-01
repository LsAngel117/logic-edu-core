package com.logossystemsit.logiceducore.domain.academic.enrollment.model;

import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.time.Instant;
import java.util.Objects;

public final class Enrollment {

    private final EnrollmentId id;
    private final UserId userId;
    private final GroupId groupId;
    private final EnrollmentStatus status;
    private final Instant enrolledAt;
    private final Instant updatedAt;

    private Enrollment(
            EnrollmentId id,
            UserId userId,
            GroupId groupId,
            EnrollmentStatus status,
            Instant enrolledAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.groupId = Objects.requireNonNull(groupId, "groupId is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.enrolledAt = Objects.requireNonNull(enrolledAt, "enrolledAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Enrollment create(
            EnrollmentId id,
            UserId userId,
            GroupId groupId,
            Instant now
    ) {
        return new Enrollment(
                id, userId, groupId,
                EnrollmentStatus.ACTIVE,
                now, now
        );
    }

    public static Enrollment restore(
            EnrollmentId id,
            UserId userId,
            GroupId groupId,
            EnrollmentStatus status,
            Instant enrolledAt,
            Instant updatedAt
    ) {
        return new Enrollment(
                id, userId, groupId,
                status,
                enrolledAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public Enrollment drop(Instant now) {
        if (this.status == EnrollmentStatus.DROPPED) {
            return this;
        }

        return new Enrollment(
                this.id, this.userId, this.groupId,
                EnrollmentStatus.DROPPED,
                this.enrolledAt, now
        );
    }

    /* ---------- EQUALS / HASHCODE ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ---------- GETTERS ---------- */

    public EnrollmentId getId() { return id; }
    public UserId getUserId() { return userId; }
    public GroupId getGroupId() { return groupId; }
    public EnrollmentStatus getStatus() { return status; }
    public Instant getEnrolledAt() { return enrolledAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
