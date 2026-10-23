package com.auth.infrastructure.user.refresh.persistence.repository;

import java.time.Instant;
import java.util.Optional;

import com.auth.infrastructure.user.refresh.persistence.entity.UserRefreshEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

public interface UserRefreshRepository extends IBaseRepository<UserRefreshEntity> {

    Optional<UserRefreshEntity> findByTokenAndRevokedFalseAndExpiredDateAfter(
        String token,
        Instant now);

    long deleteByToken(String token);

    long deleteByExpiredDateBefore(Instant now);
}
