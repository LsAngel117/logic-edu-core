package com.logossystemsit.logiceducore.infrastructure.user.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}