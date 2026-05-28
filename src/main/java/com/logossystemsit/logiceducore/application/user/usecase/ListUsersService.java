package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.ListUsersUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListUsersService implements ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResult> execute() {
        return userRepository.findAll()
                .stream()
                .map(UserResult::from)
                .toList();
    }
}
