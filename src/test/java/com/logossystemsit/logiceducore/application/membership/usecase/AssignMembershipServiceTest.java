package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.AssignMembershipCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.AssignMembershipUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignMembershipServiceTest {

    @Mock
    private MembershipRepository repository;

    private AssignMembershipUseCase useCase;

    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        useCase = new AssignMembershipService(repository);
    }

    @Test
    void execute_shouldSaveMembership() {
        AssignMembershipCommand command = new AssignMembershipCommand(USER_ID, Role.STUDENT, Scope.course("course-123"));

        useCase.execute(command);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        Membership saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
        assertThat(saved.getScope()).isEqualTo(Scope.course("course-123"));
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void execute_shouldThrowForIncompatibleRoleScope() {
        AssignMembershipCommand command = new AssignMembershipCommand(
                USER_ID, Role.PLATFORM_ADMIN, Scope.course("course-123"));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be assigned to scope");
    }

    @Test
    void execute_shouldAcceptPlatformAdminOnPlatform() {
        AssignMembershipCommand command = new AssignMembershipCommand(
                USER_ID, Role.PLATFORM_ADMIN, Scope.platform());

        useCase.execute(command);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.PLATFORM_ADMIN);
        assertThat(captor.getValue().getScope().isPlatform()).isTrue();
    }

    @Test
    void execute_shouldAcceptSchoolAdminOnSchool() {
        AssignMembershipCommand command = new AssignMembershipCommand(
                USER_ID, Role.SCHOOL_ADMIN, Scope.school("school-1"));

        useCase.execute(command);

        verify(repository).save(any(Membership.class));
    }

    @Test
    void execute_shouldAcceptTeacherOnCourse() {
        AssignMembershipCommand command = new AssignMembershipCommand(
                USER_ID, Role.TEACHER, Scope.course("course-1"));

        useCase.execute(command);

        verify(repository).save(any(Membership.class));
    }
}
