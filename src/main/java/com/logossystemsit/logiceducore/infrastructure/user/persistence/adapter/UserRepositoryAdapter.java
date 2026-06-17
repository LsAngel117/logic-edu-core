package com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter;

import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.entity.UserEntity;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        UserEntity entity = mapToEntity(user);
        jpa.save(entity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.getValue())
                .map(this::mapToDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpa.existsByUsername(username.getValue());
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return jpa.findByUsername(username.getValue())
                .map(this::mapToDomain);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    private UserEntity mapToEntity(User user) {
        UserEntity e = new UserEntity();

        e.setId(user.getId().toString());
        e.setUsername(user.getUsername().getValue());
        e.setEmail(user.getEmail().getValue());
        e.setPasswordHash(user.getPasswordHash().value());

        e.setFirstGivenName(user.getName().getFirstGivenName());
        e.setSecondGivenName(user.getName().getSecondGivenName());
        e.setFirstFamilyName(user.getName().getFirstFamilyName());
        e.setSecondFamilyName(user.getName().getSecondFamilyName());

        e.setSex(user.getSex());
        e.setBirthDate(user.getBirthDate());

        e.setDocumentType(user.getDocument().typeValue());
        e.setDocumentValue(user.getDocument().numberValue());

        e.setStatus(user.getStatus());
        e.setCreatedAt(user.getCreatedAt());
        e.setUpdatedAt(user.getUpdatedAt());

        e.setPhone(user.getPhone() != null ? user.getPhone().value() : null);
        e.setAddress(user.getAddress() != null ? user.getAddress().value() : null);
        e.setCity(user.getCity() != null ? user.getCity().value() : null);
        e.setCountry(user.getCountry() != null ? user.getCountry().value() : null);

        return e;
    }

    private User mapToDomain(UserEntity e) {
        return User.restore(
                new UserId(e.getId()),
                new Username(e.getUsername()),
                new Email(e.getEmail()),
                new PasswordHash(e.getPasswordHash()),
                new Name(
                        e.getFirstGivenName(),
                        e.getSecondGivenName(),
                        e.getFirstFamilyName(),
                        e.getSecondFamilyName()
                ),
                e.getSex(),
                e.getBirthDate(),
                new Document(
                        Document.DocumentType.valueOf(e.getDocumentType()),
                        new DocumentNumber(e.getDocumentValue())
                ),
                e.getPhone() != null ? Phone.of(e.getPhone()) : null,
                e.getAddress() != null ? Address.of(e.getAddress()) : null,
                e.getCity() != null ? new City(e.getCity()) : null,
                e.getCountry() != null ? new Country(e.getCountry()) : null,
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
