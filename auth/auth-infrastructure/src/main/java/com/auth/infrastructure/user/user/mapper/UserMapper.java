package com.auth.infrastructure.user.user.mapper;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;
import com.auth.infrastructure.user.user.jpa.entity.UserJpaEntity;
import com.auth.infrastructure.user.user.mongo.document.UserMongoDocument;
import com.base.infrastructure.mapper.BaseMapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends BaseMapper<UserJpaEntity, User> {

    UserLight toDomainLight(UserMongoDocument entity);
}