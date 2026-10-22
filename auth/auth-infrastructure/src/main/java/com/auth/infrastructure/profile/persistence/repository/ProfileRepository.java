package com.auth.infrastructure.profile.persistence.repository;

import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends IBaseRepository<ProfileEntity> {

}