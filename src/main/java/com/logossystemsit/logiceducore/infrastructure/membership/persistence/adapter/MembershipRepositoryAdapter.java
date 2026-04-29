package com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter;

import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.entity.MembershipEntity;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;

import java.util.List;
import java.util.Optional;

public class MembershipRepositoryAdapter implements MembershipRepository {

    private final MembershipJpaRepository jpa;

    public MembershipRepositoryAdapter(MembershipJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Membership membership) {
        jpa.save(mapToEntity(membership));
    }

    @Override
    public Optional<Membership> findById(MembershipId id) {
        return Optional.empty();
    }

    @Override
    public List<Membership> findByUserId(UserId userId) {
        return jpa.findByUserId(userId.value())
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    private MembershipEntity mapToEntity(Membership m) {
        MembershipEntity e = new MembershipEntity();

        e.setId(m.getId().value());
        e.setUserId(m.getUserId().value());
        e.setRole(m.getRole().name());

        e.setScopeType(m.getScope().type().name());
        e.setScopeRefId(m.getScope().referenceId().orElse(null));

        e.setActive(m.isActive());

        return e;
    }

    private Membership mapToDomain(MembershipEntity e) {
        return Membership.restore(
                new MembershipId(e.getId()),
                new UserId(e.getUserId()),
                Role.valueOf(e.getRole()),
                Scope.from(
                        Scope.Type.valueOf(e.getScopeType()),
                        e.getScopeRefId()
                ),
                e.isActive()
        );
    }
}
