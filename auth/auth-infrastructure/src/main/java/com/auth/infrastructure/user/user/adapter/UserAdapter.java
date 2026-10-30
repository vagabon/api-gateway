package com.auth.infrastructure.user.user.adapter;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;
import com.auth.infrastructure.user.user.mapper.UserMapper;
import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.auth.infrastructure.user.user.persistence.repository.UserRepository;
import com.base.infrastructure.adapter.BaseCrudAdapter;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter extends BaseCrudAdapter<UserEntity, User>
    implements UserRepositoryPort {

    @Getter
    private final UserRepository repository;
    @Getter
    private final UserMapper mapper;

    @Override
    public User findByGoogleId(String googleId) {
        var userEntity = repository.findByGoogleId(googleId)
            .orElse(null);
        return mapper.toDomain(userEntity);
    }

    @Override
    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }
}