package com.auth.infrastructure.user.user.persistence.repository;

import java.util.Optional;

import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends IBaseRepository<UserEntity> {
    Optional<UserEntity> findByGoogleId(String googleId);
}