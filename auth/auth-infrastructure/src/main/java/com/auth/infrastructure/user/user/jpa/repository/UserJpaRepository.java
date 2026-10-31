package com.auth.infrastructure.user.user.jpa.repository;

import java.util.Optional;

import com.auth.infrastructure.user.user.jpa.entity.UserJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends BaseJpaRepository<UserJpaEntity> {
    Optional<UserJpaEntity> findByGoogleId(String googleId);
}