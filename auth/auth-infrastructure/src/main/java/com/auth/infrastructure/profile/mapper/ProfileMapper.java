package com.auth.infrastructure.profile.mapper;

import com.auth.domain.profile.model.Profile;
import com.auth.infrastructure.profile.persistence.entity.ProfileEntity;
import com.base.infrastructure.mapper.BaseMapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper extends BaseMapper<ProfileEntity, Profile> {
}