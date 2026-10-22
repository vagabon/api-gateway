package com.auth.infrastructure.user.mapper;

import com.auth.domain.user.model.User;
import com.auth.infrastructure.user.persistence.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // Conversion Entity -> Domain
    User toDomain(UserEntity userEntity);

    // Conversion Domain -> Entity
    UserEntity toEntity(User user);
}