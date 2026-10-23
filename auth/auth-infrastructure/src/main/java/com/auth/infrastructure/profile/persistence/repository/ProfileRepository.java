package com.auth.infrastructure.profile.persistence.repository;

import java.util.Optional;

import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends IBaseRepository<ProfileEntity> {

    public Optional<ProfileEntity> findByName(String name);

}