package com.logossystemsit.logiceducore.domain.membership.model.valueobject;

import java.util.EnumSet;
import java.util.Set;

public enum Role {

    PLATFORM_ADMIN("Platform admin", EnumSet.of(Scope.Type.PLATFORM)),
    SCHOOL_ADMIN("School admin", EnumSet.of(Scope.Type.SCHOOL)),
    BRANCH_ADMIN("Branch admin", EnumSet.of(Scope.Type.BRANCH)),
    TEACHER("Teacher", EnumSet.of(Scope.Type.COURSE)),
    STUDENT("Student", EnumSet.of(Scope.Type.COURSE));

    private final String displayName;
    private final Set<Scope.Type> allowedScopeTypes;

    Role(String displayName, Set<Scope.Type> allowedScopeTypes) {
        this.displayName = displayName;
        this.allowedScopeTypes = Set.copyOf(allowedScopeTypes);
    }

    public String displayName() {
        return displayName;
    }

    public boolean supports(Scope.Type scopeType) {
        return allowedScopeTypes.contains(scopeType);
    }

    public Set<Scope.Type> allowedScopeTypes() {
        return allowedScopeTypes;
    }

    public boolean isPlatformAdmin() {
        return this == PLATFORM_ADMIN;
    }

    public boolean isSchoolAdmin() {
        return this == SCHOOL_ADMIN;
    }

    public boolean isBranchAdmin() {
        return this == BRANCH_ADMIN;
    }

    public boolean isTeacher() {
        return this == TEACHER;
    }

    public boolean isStudent() {
        return this == STUDENT;
    }
}
