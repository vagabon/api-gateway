package com.auth.infrastructure.user.refresh.mapper;

import com.auth.domain.user.refresh.model.UserRefresh;
import com.auth.infrastructure.user.refresh.jpa.entity.UserRefreshJpaEntity;
import com.base.infrastructure.mapper.BaseMapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserRefreshMapper extends BaseMapper<UserRefreshJpaEntity, UserRefresh> {

}
