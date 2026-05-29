package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.port.in.ToggleMembershipUseCase;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleMembershipServiceTest {

    @Mock
    private MembershipRepository repository;

    private ToggleMembershipUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ToggleMembershipService(repository);
    }

    private static Membership activeMembership(MembershipId id) {
        return Membership.restore(id,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.STUDENT, Scope.course("course-1"), true);
    }

    private static Membership inactiveMembership(MembershipId id) {
        return Membership.restore(id,
                new UserId("123e4567-e89b-12d3-a456-426614174000"),
                Role.STUDENT, Scope.course("course-1"), false);
    }

    @Test
    void deactivate_shouldSaveDeactivatedMembership() {
        MembershipId id = MembershipId.generate();
        MembershipId otherId = MembershipId.generate();
        UserId userId = new UserId("123e4567-e89b-12d3-a456-426614174000");
        Membership active = Membership.restore(id, userId, Role.STUDENT, Scope.course("course-1"), true);
        Membership other = Membership.restore(otherId, userId, Role.STUDENT, Scope.course("course-2"), true);
        when(repository.findById(id)).thenReturn(Optional.of(active));
        when(repository.findByUserId(userId)).thenReturn(List.of(active, other));

        useCase.deactivate(id);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    void activate_shouldSaveActivatedMembership() {
        MembershipId id = MembershipId.generate();
        Membership inactive = inactiveMembership(id);
        when(repository.findById(id)).thenReturn(Optional.of(inactive));

        useCase.activate(id);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    void deactivate_shouldThrowWhenMembershipNotFound() {
        MembershipId id = MembershipId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deactivate(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void activate_shouldThrowWhenMembershipNotFound() {
        MembershipId id = MembershipId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.activate(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deactivate_shouldRejectLastActiveMembership() {
        MembershipId id = MembershipId.generate();
        UserId userId = new UserId("223e4567-e89b-12d3-a456-426614174001");
        Membership onlyActive = Membership.restore(id, userId, Role.STUDENT, Scope.course("course-1"), true);
        when(repository.findById(id)).thenReturn(Optional.of(onlyActive));
        when(repository.findByUserId(userId)).thenReturn(List.of(onlyActive));

        assertThatThrownBy(() -> useCase.deactivate(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot deactivate last active membership");
    }

    @Test
    void deactivate_shouldAllowWhenMultipleActiveMemberships() {
        MembershipId targetId = MembershipId.generate();
        MembershipId otherId = MembershipId.generate();
        UserId userId = new UserId("323e4567-e89b-12d3-a456-426614174002");
        Membership target = Membership.restore(targetId, userId, Role.STUDENT, Scope.course("course-1"), true);
        Membership other = Membership.restore(otherId, userId, Role.STUDENT, Scope.course("course-2"), true);
        when(repository.findById(targetId)).thenReturn(Optional.of(target));
        when(repository.findByUserId(userId)).thenReturn(List.of(target, other));

        useCase.deactivate(targetId);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }
}
