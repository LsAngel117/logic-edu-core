package com.logossystemsit.logiceducore.application.user.port.out;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.Email;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.Username;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);

    List<User> findAll();

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    boolean existsByUsername(Username username);
}