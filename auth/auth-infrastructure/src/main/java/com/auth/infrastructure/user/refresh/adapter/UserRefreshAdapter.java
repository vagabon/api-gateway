package com.auth.infrastructure.user.refresh.adapter;

import java.time.Instant;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.domain.user.refresh.port.UserRefreshRepositoryPort;
import com.auth.infrastructure.user.refresh.jpa.entity.UserRefreshJpaEntity;
import com.auth.infrastructure.user.refresh.jpa.repository.UserRefreshJpaRepository;
import com.auth.infrastructure.user.refresh.mapper.UserRefreshMapper;
import com.base.infrastructure.adapter.CreateAdapter;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRefreshAdapter
    implements UserRefreshRepositoryPort,
    CreateAdapter<UserRefreshJpaEntity, UserRefresh> {

    @Getter
    private final UserRefreshJpaRepository repository;
    @Getter
    private final UserRefreshMapper mapper;

    @Override
    public UserRefresh findByToken(String token) {
        var userRefresh = repository
            .findByTokenAndRevokedFalseAndExpiredDateAfter(token, Instant.now())
            .orElse(null);
        return mapper.toDomain(userRefresh);
    }

    @Override
    public long deleteUserRefreshByToken(String token) {
        return repository.deleteByToken(token);
    }

    @Override
    public long deleteUserRefreshWhenExpired() {
        return repository.deleteByExpiredDateBefore(Instant.now());
    }

}
