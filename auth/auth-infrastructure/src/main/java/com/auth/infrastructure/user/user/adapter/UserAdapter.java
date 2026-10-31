package com.auth.infrastructure.user.user.adapter;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;
import com.auth.infrastructure.user.user.jpa.entity.UserJpaEntity;
import com.auth.infrastructure.user.user.jpa.repository.UserJpaRepository;
import com.auth.infrastructure.user.user.mapper.UserMapper;
import com.base.infrastructure.adapter.BaseCrudAdapter;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter extends BaseCrudAdapter<UserJpaEntity, User>
    implements UserRepositoryPort {

    @Getter
    private final UserJpaRepository repository;
    @Getter
    private final UserMapper mapper;

    @Override
    public User findByGoogleId(String googleId) {
        var userEntity = repository.findByGoogleId(googleId)
            .orElse(null);
        return mapper.toDomain(userEntity);
    }

    @Override
    public Class<UserJpaEntity> getEntityClass() {
        return UserJpaEntity.class;
    }
}