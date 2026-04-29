package com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.membership.persistence.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, String> {
    List<MembershipEntity> findByUserId(String userId);
}
