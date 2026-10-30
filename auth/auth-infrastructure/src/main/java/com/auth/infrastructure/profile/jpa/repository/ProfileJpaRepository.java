package com.auth.infrastructure.profile.jpa.repository;

import java.util.Optional;

import com.auth.infrastructure.profile.jpa.entity.ProfileJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProfileJpaRepository extends BaseJpaRepository<ProfileJpaEntity> {

    public Optional<ProfileJpaEntity> findByName(String name);

}