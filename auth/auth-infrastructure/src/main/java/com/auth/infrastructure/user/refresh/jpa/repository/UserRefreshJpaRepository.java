package com.auth.infrastructure.user.refresh.jpa.repository;

import java.time.Instant;
import java.util.Optional;

import com.auth.infrastructure.user.refresh.jpa.entity.UserRefreshJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;

public interface UserRefreshJpaRepository
    extends BaseJpaRepository<UserRefreshJpaEntity> {

    Optional<UserRefreshJpaEntity> findByToken(String token);

    long deleteByToken(String token);

    long deleteByExpiredDateBefore(Instant now);
}
