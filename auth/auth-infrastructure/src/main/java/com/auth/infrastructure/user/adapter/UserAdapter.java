package com.auth.infrastructure.user.adapter;

import java.util.Optional;

import com.auth.domain.user.model.User;
import com.auth.domain.user.port.IUserAdaptaterPort;
import com.auth.infrastructure.user.mapper.UserMapper;
import com.auth.infrastructure.user.persistence.repository.UserRepository;

import org.springframework.stereotype.Component;

@Component
public class UserAdapter implements IUserAdaptaterPort {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserAdapter(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}