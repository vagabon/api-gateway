package com.auth.infrastructure.user.user.mapper;

import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.base.infrastructure.mapper.BaseMapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends BaseMapper<UserEntity, User> {
}