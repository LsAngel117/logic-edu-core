package com.logossystemsit.logiceducore.application.user.port.out;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);
}