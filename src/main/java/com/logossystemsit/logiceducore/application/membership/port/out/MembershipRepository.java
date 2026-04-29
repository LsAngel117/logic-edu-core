package com.logossystemsit.logiceducore.application.membership.port.out;

import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository {

    void save(Membership membership);

    Optional<Membership> findById(MembershipId id);

    List<Membership> findByUserId(UserId userId);
}
