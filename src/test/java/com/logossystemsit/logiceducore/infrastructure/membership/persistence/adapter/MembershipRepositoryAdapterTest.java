package com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter;

import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.entity.MembershipEntity;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipRepositoryAdapterTest {

    @Mock
    private MembershipJpaRepository jpa;

    private MembershipRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MembershipRepositoryAdapter(jpa);
    }

    @Test
    void findById_delegatesToJpaRepositoryAndMapsResult() {
        var membershipId = new MembershipId("223e4567-e89b-12d3-a456-426614174001");
        var entity = buildEntity(
                membershipId.value(),
                "123e4567-e89b-12d3-a456-426614174000",
                "PLATFORM_ADMIN",
                "PLATFORM",
                null
        );
        when(jpa.findById(membershipId.value())).thenReturn(Optional.of(entity));

        Optional<Membership> result = adapter.findById(membershipId);

        assertThat(result).isPresent();
        assertThat(result.get().getId().value()).isEqualTo(membershipId.value());
        assertThat(result.get().getRole()).isEqualTo(Role.PLATFORM_ADMIN);
        assertThat(result.get().getScope().isPlatform()).isTrue();
        verify(jpa).findById(membershipId.value());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        var membershipId = new MembershipId("323e4567-e89b-12d3-a456-426614174002");
        when(jpa.findById(membershipId.value())).thenReturn(Optional.empty());

        Optional<Membership> result = adapter.findById(membershipId);

        assertThat(result).isEmpty();
        verify(jpa).findById(membershipId.value());
    }

    private MembershipEntity buildEntity(String id, String userId, String role, String scopeType, String scopeRefId) {
        MembershipEntity e = new MembershipEntity();
        e.setId(id);
        e.setUserId(userId);
        e.setRole(role);
        e.setScopeType(scopeType);
        e.setScopeRefId(scopeRefId);
        e.setActive(true);
        return e;
    }
}
