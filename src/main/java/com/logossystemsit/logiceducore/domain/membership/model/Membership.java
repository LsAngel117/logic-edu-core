package com.logossystemsit.logiceducore.domain.membership.model;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.Objects;

public final class Membership {

    private final MembershipId id;
    private final UserId userId;
    private final Role role;
    private final Scope scope;
    private final boolean active;

    private Membership(MembershipId id, UserId userId, Role role, Scope scope, boolean active) {
        this.id = Objects.requireNonNull(id, "MembershipId is required");
        this.userId = Objects.requireNonNull(userId, "UserId is required");
        this.role = Objects.requireNonNull(role, "Role is required");
        this.scope = Objects.requireNonNull(scope, "Scope is required");
        this.active = active;

        validateConsistency();
    }

    public static Membership create(UserId userId, Role role, Scope scope) {
        return new Membership(MembershipId.generate(), userId, role, scope, true);
    }

    public static Membership restore(MembershipId id, UserId userId, Role role, Scope scope, boolean active) {
        return new Membership(id, userId, role, scope, active);
    }

    private void validateConsistency() {
        if (!role.supports(scope.type())) {
            throw new IllegalArgumentException(
                    role.name() + " cannot be assigned to scope " + scope.type()
            );
        }

        if (role.isPlatformAdmin() && !scope.isPlatform()) {
            throw new IllegalArgumentException("PLATFORM_ADMIN must have PLATFORM scope");
        }

        if ((role.isSchoolAdmin() && !scope.isSchool())
                || (role.isTeacher() && !scope.isCourse())
                || (role.isStudent() && !scope.isCourse())) {
            throw new IllegalArgumentException("Role and scope do not match");
        }
    }

    public Membership activate() {
        if (active) {
            return this;
        }
        return new Membership(id, userId, role, scope, true);
    }

    public Membership deactivate() {
        if (!active) {
            return this;
        }
        return new Membership(id, userId, role, scope, false);
    }

    public Membership changeRole(Role newRole) {
        Objects.requireNonNull(newRole, "Role is required");
        return new Membership(id, userId, newRole, scope, active);
    }

    public Membership changeScope(Scope newScope) {
        Objects.requireNonNull(newScope, "Scope is required");
        return new Membership(id, userId, role, newScope, active);
    }

    public MembershipId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isPlatformAdmin() {
        return active && role.isPlatformAdmin();
    }

    public boolean isSchoolAdmin() {
        return active && role.isSchoolAdmin();
    }

    public boolean isTeacher() {
        return active && role.isTeacher();
    }

    public boolean isStudent() {
        return active && role.isStudent();
    }
}
