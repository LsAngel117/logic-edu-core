package com.logossystemsit.logiceducore.application.membership.policy;

import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;

import java.util.Objects;

public final class MembershipAssignmentValidator {

    public void validateBranchMembership(Role role, Scope scope, Branch branch, SchoolId expectedSchoolId) {
        Objects.requireNonNull(role, "Role is required");
        Objects.requireNonNull(scope, "Scope is required");

        if (!role.supports(scope.type())) {
            throw new IllegalArgumentException(
                    role.name() + " cannot be assigned to scope " + scope.type()
            );
        }

        if (!scope.isBranch()) {
            return;
        }

        if (branch == null) {
            throw new IllegalArgumentException("Branch is required for BRANCH scope");
        }

        if (!branch.isActive()) {
            throw new IllegalStateException("Cannot assign membership to an inactive branch");
        }

        if (expectedSchoolId != null && !branch.getSchoolId().equals(expectedSchoolId)) {
            throw new IllegalArgumentException("Branch does not belong to the expected school");
        }

        if (role != Role.BRANCH_ADMIN && role != Role.TEACHER && role != Role.STUDENT) {
            throw new IllegalArgumentException("Role is not allowed for branch scope");
        }
    }
}
