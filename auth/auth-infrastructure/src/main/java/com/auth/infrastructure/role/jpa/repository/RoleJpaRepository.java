package com.auth.infrastructure.role.jpa.repository;

import java.util.Optional;

import com.auth.infrastructure.role.jpa.entity.RoleJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends BaseJpaRepository<RoleJpaEntity> {

    public Optional<RoleJpaEntity> findByRoleNameAndOrigin(String role, String origin);

}