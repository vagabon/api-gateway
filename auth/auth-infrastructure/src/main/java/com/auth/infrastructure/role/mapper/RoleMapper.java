package com.auth.infrastructure.role.mapper;

import com.auth.domain.role.model.Role;
import com.auth.infrastructure.role.jpa.entity.RoleJpaEntity;
import com.base.infrastructure.mapper.BaseMapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper extends BaseMapper<RoleJpaEntity, Role> {
}