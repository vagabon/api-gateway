package com.auth.infrastructure.user.persistence.repository;

import com.auth.infrastructure.user.persistence.entity.UserEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends IBaseRepository<UserEntity> {
}