package com.logossystemsit.logiceducore.application.membership.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipRoleCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.ChangeMembershipRoleUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeMembershipRoleServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    private ChangeMembershipRoleUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangeMembershipRoleService(membershipRepository);
    }

    @Test
    void execute_shouldChangeRoleAndSave() {
        MembershipId membershipId = MembershipId.generate();
        Membership membership = Membership.restore(
                membershipId,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.STUDENT,
                Scope.course("course-123"),
                true
        );
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.of(membership));

        useCase.execute(new ChangeMembershipRoleCommand(membershipId, Role.TEACHER));

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());
        Membership saved = captor.getValue();
        assertThat(saved.getRole()).isEqualTo(Role.TEACHER);
        assertThat(saved.getId()).isEqualTo(membershipId);
    }

    @Test
    void execute_shouldThrowWhenMembershipNotFound() {
        MembershipId membershipId = MembershipId.generate();
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new ChangeMembershipRoleCommand(membershipId, Role.TEACHER)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Membership not found");
    }

    @Test
    void execute_shouldThrowWhenRoleIncompatibleWithScope() {
        MembershipId membershipId = MembershipId.generate();
        Membership membership = Membership.restore(
                membershipId,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.STUDENT,
                Scope.course("course-123"),
                true
        );
        when(membershipRepository.findById(membershipId)).thenReturn(Optional.of(membership));

        // PLATFORM_ADMIN is not supported in COURSE scope -> changeRole throws
        assertThatThrownBy(() ->
                useCase.execute(new ChangeMembershipRoleCommand(membershipId, Role.PLATFORM_ADMIN)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be assigned to scope");
    }
}
