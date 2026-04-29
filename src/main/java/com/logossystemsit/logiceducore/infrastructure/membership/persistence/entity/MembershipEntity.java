package com.logossystemsit.logiceducore.infrastructure.membership.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "memberships")
public class MembershipEntity {

    @Id
    private String id;

    private String userId;

    private String role;

    // Scope embebido
    private String scopeType;
    private String scopeRefId;

    private boolean active;

    /* ------------------ GETTERS ------------------ */

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getScopeType() {
        return scopeType;
    }

    public String getScopeRefId() {
        return scopeRefId;
    }

    public boolean isActive() {
        return active;
    }
    /* ------------------ SETTERS ------------------ */

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public void setScopeRefId(String scopeRefId) {
        this.scopeRefId = scopeRefId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

