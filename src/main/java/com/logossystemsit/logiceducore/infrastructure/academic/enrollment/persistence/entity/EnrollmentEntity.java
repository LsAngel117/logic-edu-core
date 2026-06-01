package com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "enrollments")
public class EnrollmentEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(nullable = false)
    private String status;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getGroupId() { return groupId; }
    public String getStatus() { return status; }
    public Instant getEnrolledAt() { return enrolledAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setStatus(String status) { this.status = status; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
