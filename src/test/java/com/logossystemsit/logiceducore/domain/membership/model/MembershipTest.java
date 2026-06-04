package com.logossystemsit.logiceducore.domain.membership.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MembershipTest {

    private static UserId userId() { return new UserId("123e4567-e89b-12d3-a456-426614174000"); }
    private static Role role() { return Role.STUDENT; }
    private static Scope courseScope() { return Scope.course("course-123"); }
    private static Scope platformScope() { return Scope.platform(); }
    private static Scope schoolScope() { return Scope.school("school-1"); }
    private static Scope branchScope() { return Scope.branch("branch-1"); }
    private static Scope academyScope() { return Scope.academy("academy-1"); }

    // ==================== create() factory ====================

    @Nested
    class CreateFactory {

        @Test
        void shouldCreateActiveMembership() {
            Membership m = Membership.create(userId(), Role.STUDENT, courseScope());

            assertThat(m.getId()).isNotNull();
            assertThat(m.getUserId()).isEqualTo(userId());
            assertThat(m.getRole()).isEqualTo(Role.STUDENT);
            assertThat(m.getScope()).isEqualTo(courseScope());
            assertThat(m.isActive()).isTrue();
        }

        @Test
        void shouldCreateWithPlatformAdminRole() {
            Membership m = Membership.create(userId(), Role.PLATFORM_ADMIN, platformScope());

            assertThat(m.getRole()).isEqualTo(Role.PLATFORM_ADMIN);
            assertThat(m.getScope().isPlatform()).isTrue();
            assertThat(m.isActive()).isTrue();
        }

        @Test
        void shouldRejectTeacherWithSchoolScope() {
            assertThatThrownBy(() -> Membership.create(userId(), Role.TEACHER, schoolScope()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("TEACHER cannot be assigned to scope SCHOOL");
        }

        @Test
        void shouldRejectSchoolAdminWithCourseScope() {
            assertThatThrownBy(() -> Membership.create(userId(), Role.SCHOOL_ADMIN, courseScope()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("SCHOOL_ADMIN cannot be assigned to scope COURSE");
        }

        @Test
        void shouldRejectBranchAdminWithSchoolScope() {
            assertThatThrownBy(() -> Membership.create(userId(), Role.BRANCH_ADMIN, schoolScope()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("BRANCH_ADMIN cannot be assigned to scope SCHOOL");
        }

        @Test
        void shouldRejectPlatformAdminWithCourseScope() {
            assertThatThrownBy(() -> Membership.create(userId(), Role.PLATFORM_ADMIN, courseScope()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("PLATFORM_ADMIN cannot be assigned to scope COURSE");
        }
    }

    // ==================== activate() / deactivate() ====================

    @Nested
    class Toggle {

        @Test
        void shouldActivateInactiveMembership() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), false);

            Membership activated = m.activate();

            assertThat(activated.isActive()).isTrue();
        }

        @Test
        void activateShouldReturnSameInstanceWhenAlreadyActive() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            Membership result = m.activate();

            assertThat(result).isSameAs(m);
        }

        @Test
        void shouldDeactivateActiveMembership() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            Membership deactivated = m.deactivate();

            assertThat(deactivated.isActive()).isFalse();
        }

        @Test
        void deactivateShouldReturnSameInstanceWhenAlreadyInactive() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), false);

            Membership result = m.deactivate();

            assertThat(result).isSameAs(m);
        }
    }

    // ==================== changeRole() ====================

    @Nested
    class ChangeRole {

        @Test
        void shouldChangeRoleSuccessfully() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            Membership updated = m.changeRole(Role.TEACHER);

            assertThat(updated.getRole()).isEqualTo(Role.TEACHER);
            assertThat(updated.getScope()).isEqualTo(courseScope());
            assertThat(updated.isActive()).isTrue();
        }

        @Test
        void shouldRejectNullRole() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            assertThatThrownBy(() -> m.changeRole(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Role");
        }

        @Test
        void shouldRejectIncompatibleRole() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            assertThatThrownBy(() -> m.changeRole(Role.PLATFORM_ADMIN))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("cannot be assigned to scope");
        }
    }

    // ==================== changeScope() ====================

    @Nested
    class ChangeScope {

        @Test
        void shouldChangeScopeSuccessfully() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.TEACHER, Scope.course("course-1"), true);

            Membership updated = m.changeScope(Scope.course("course-2"));

            assertThat(updated.getScope()).isEqualTo(Scope.course("course-2"));
            assertThat(updated.getRole()).isEqualTo(Role.TEACHER);
        }

        @Test
        void shouldRejectNullScope() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            assertThatThrownBy(() -> m.changeScope(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Scope");
        }

        @Test
        void shouldRejectIncompatibleScope() {
            Membership m = Membership.restore(MembershipId.generate(), userId(),
                    Role.TEACHER, courseScope(), true);

            assertThatThrownBy(() -> m.changeScope(schoolScope()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("TEACHER cannot be assigned to scope SCHOOL");
        }
    }

    // ==================== Role enum ====================

    @Nested
    class RoleEnum {

        @Test
        void platformAdminShouldSupportPlatformOnly() {
            assertThat(Role.PLATFORM_ADMIN.supports(Scope.Type.PLATFORM)).isTrue();
            assertThat(Role.PLATFORM_ADMIN.supports(Scope.Type.SCHOOL)).isFalse();
            assertThat(Role.PLATFORM_ADMIN.supports(Scope.Type.COURSE)).isFalse();
        }

        @Test
        void schoolAdminShouldSupportSchoolOnly() {
            assertThat(Role.SCHOOL_ADMIN.supports(Scope.Type.SCHOOL)).isTrue();
            assertThat(Role.SCHOOL_ADMIN.supports(Scope.Type.PLATFORM)).isFalse();
            assertThat(Role.SCHOOL_ADMIN.supports(Scope.Type.COURSE)).isFalse();
        }

        @Test
        void branchAdminShouldSupportBranchOnly() {
            assertThat(Role.BRANCH_ADMIN.supports(Scope.Type.BRANCH)).isTrue();
            assertThat(Role.BRANCH_ADMIN.supports(Scope.Type.SCHOOL)).isFalse();
        }

        @Test
        void teacherShouldSupportCourseOnly() {
            assertThat(Role.TEACHER.supports(Scope.Type.COURSE)).isTrue();
            assertThat(Role.TEACHER.supports(Scope.Type.SCHOOL)).isFalse();
            assertThat(Role.TEACHER.supports(Scope.Type.PLATFORM)).isFalse();
        }

        @Test
        void studentShouldSupportCourseOnly() {
            assertThat(Role.STUDENT.supports(Scope.Type.COURSE)).isTrue();
            assertThat(Role.STUDENT.supports(Scope.Type.SCHOOL)).isFalse();
        }

        @Test
        void allowedScopeTypesShouldReturnCorrectSet() {
            assertThat(Role.PLATFORM_ADMIN.allowedScopeTypes()).containsExactly(Scope.Type.PLATFORM);
            assertThat(Role.SCHOOL_ADMIN.allowedScopeTypes()).containsExactly(Scope.Type.SCHOOL);
            assertThat(Role.BRANCH_ADMIN.allowedScopeTypes()).containsExactly(Scope.Type.BRANCH);
            assertThat(Role.TEACHER.allowedScopeTypes()).containsExactly(Scope.Type.COURSE);
            assertThat(Role.STUDENT.allowedScopeTypes()).containsExactly(Scope.Type.COURSE);
        }

        @Test
        void identityChecksShouldWork() {
            assertThat(Role.PLATFORM_ADMIN.isPlatformAdmin()).isTrue();
            assertThat(Role.PLATFORM_ADMIN.isTeacher()).isFalse();
            assertThat(Role.TEACHER.isTeacher()).isTrue();
            assertThat(Role.STUDENT.isStudent()).isTrue();
            assertThat(Role.SCHOOL_ADMIN.isSchoolAdmin()).isTrue();
            assertThat(Role.BRANCH_ADMIN.isBranchAdmin()).isTrue();
        }
    }

    // ==================== Scope factory methods ====================

    @Nested
    class ScopeFactory {

        @Test
        void platformShouldHaveNoRefId() {
            Scope s = Scope.platform();

            assertThat(s.type()).isEqualTo(Scope.Type.PLATFORM);
            assertThat(s.referenceId()).isEmpty();
            assertThat(s.isPlatform()).isTrue();
        }

        @Test
        void schoolShouldRequireRefId() {
            assertThatThrownBy(() -> Scope.school(null))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("schoolId");

            assertThatThrownBy(() -> Scope.school("  "))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("schoolId");
        }

        @Test
        void schoolShouldCreateWithValidRefId() {
            Scope s = Scope.school("school-1");

            assertThat(s.type()).isEqualTo(Scope.Type.SCHOOL);
            assertThat(s.referenceId()).hasValue("school-1");
            assertThat(s.isSchool()).isTrue();
        }

        @Test
        void branchShouldRequireRefId() {
            assertThatThrownBy(() -> Scope.branch(null))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("branchId");
        }

        @Test
        void branchShouldCreateWithValidRefId() {
            Scope s = Scope.branch("branch-1");

            assertThat(s.type()).isEqualTo(Scope.Type.BRANCH);
            assertThat(s.referenceId()).hasValue("branch-1");
            assertThat(s.isBranch()).isTrue();
        }

        @Test
        void courseShouldRequireRefId() {
            assertThatThrownBy(() -> Scope.course(null))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("courseId");
        }

        @Test
        void academyShouldRequireRefId() {
            assertThatThrownBy(() -> Scope.academy(null))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("academyId");
        }

        @Test
        void fromMethodShouldCreateCorrectScope() {
            Scope platform = Scope.from(Scope.Type.PLATFORM, null);
            assertThat(platform.isPlatform()).isTrue();

            Scope school = Scope.from(Scope.Type.SCHOOL, "school-1");
            assertThat(school.type()).isEqualTo(Scope.Type.SCHOOL);
            assertThat(school.referenceId()).hasValue("school-1");

            Scope branch = Scope.from(Scope.Type.BRANCH, "branch-1");
            assertThat(branch.type()).isEqualTo(Scope.Type.BRANCH);

            Scope course = Scope.from(Scope.Type.COURSE, "course-1");
            assertThat(course.type()).isEqualTo(Scope.Type.COURSE);

            Scope academy = Scope.from(Scope.Type.ACADEMY, "academy-1");
            assertThat(academy.type()).isEqualTo(Scope.Type.ACADEMY);
        }

        @Test
        void platformFromShouldIgnoreRefIdAndCreateWithoutIt() {
            Scope s = Scope.from(Scope.Type.PLATFORM, "some-ref");
            assertThat(s.type()).isEqualTo(Scope.Type.PLATFORM);
            assertThat(s.referenceId()).isEmpty();
        }
    }

    // ==================== Member helpers ====================

    @Nested
    class MemberHelpers {

        @Test
        void isPlatformAdminShouldReturnTrueOnlyForActivePlatformAdmin() {
            Membership active = Membership.restore(MembershipId.generate(), userId(),
                    Role.PLATFORM_ADMIN, platformScope(), true);
            Membership inactive = Membership.restore(MembershipId.generate(), userId(),
                    Role.PLATFORM_ADMIN, platformScope(), false);

            assertThat(active.isPlatformAdmin()).isTrue();
            assertThat(inactive.isPlatformAdmin()).isFalse();
        }

        @Test
        void isSchoolAdminShouldWork() {
            Membership active = Membership.restore(MembershipId.generate(), userId(),
                    Role.SCHOOL_ADMIN, schoolScope(), true);
            Membership wrongRole = Membership.restore(MembershipId.generate(), userId(),
                    Role.TEACHER, courseScope(), true);

            assertThat(active.isSchoolAdmin()).isTrue();
            assertThat(wrongRole.isSchoolAdmin()).isFalse();
        }

        @Test
        void isTeacherShouldWork() {
            Membership active = Membership.restore(MembershipId.generate(), userId(),
                    Role.TEACHER, courseScope(), true);

            assertThat(active.isTeacher()).isTrue();
        }

        @Test
        void isStudentShouldWork() {
            Membership active = Membership.restore(MembershipId.generate(), userId(),
                    Role.STUDENT, courseScope(), true);

            assertThat(active.isStudent()).isTrue();
        }
    }
}
