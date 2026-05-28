package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipScopeCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.ChangeMembershipScopeUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeMembershipScopeServiceTest {

    @Mock
    private MembershipRepository repository;

    private ChangeMembershipScopeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangeMembershipScopeService(repository);
    }

    @Test
    void execute_shouldChangeScopeAndSave() {
        MembershipId membershipId = MembershipId.generate();
        Membership membership = Membership.restore(
                membershipId,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.STUDENT,
                Scope.course("course-1"),
                true
        );
        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));

        useCase.execute(new ChangeMembershipScopeCommand(membershipId, Scope.course("course-2")));

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        Membership saved = captor.getValue();
        assertThat(saved.getScope()).isEqualTo(Scope.course("course-2"));
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void execute_shouldThrowWhenMembershipNotFound() {
        MembershipId membershipId = MembershipId.generate();
        when(repository.findById(membershipId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new ChangeMembershipScopeCommand(membershipId, Scope.course("new-course"))))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void execute_shouldThrowWhenScopeIncompatibleWithRole() {
        MembershipId membershipId = MembershipId.generate();
        Membership membership = Membership.restore(
                membershipId,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.TEACHER,  // TEACHER only supports COURSE
                Scope.course("course-1"),
                true
        );
        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));

        assertThatThrownBy(() ->
                useCase.execute(new ChangeMembershipScopeCommand(membershipId, Scope.school("school-1"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be assigned to scope");
    }
}
