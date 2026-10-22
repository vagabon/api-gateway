package com.auth.infrastructure.user.persistence.repository;

import java.util.Optional;

import com.auth.infrastructure.user.persistence.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}