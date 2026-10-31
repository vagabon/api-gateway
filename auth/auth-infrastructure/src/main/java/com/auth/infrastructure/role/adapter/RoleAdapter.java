package com.auth.infrastructure.role.adapter;

import com.auth.domain.role.model.Role;
import com.auth.domain.role.port.RoleRepositoryPort;
import com.auth.infrastructure.role.jpa.entity.RoleJpaEntity;
import com.auth.infrastructure.role.jpa.repository.RoleJpaRepository;
import com.auth.infrastructure.role.mapper.RoleMapper;
import com.base.domain.exception.MetierException;
import com.base.infrastructure.adapter.BaseCrudAdapter;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleAdapter
    extends BaseCrudAdapter<RoleJpaEntity, Role>
    implements RoleRepositoryPort {

    private static final String USER_ROLE = "USER";

    private static final String ERROR_PROFIL_NOT_FOUND = "ERROR.PROFILE_USER_NOT_FOUND";

    @Getter
    private final RoleJpaRepository repository;
    @Getter
    private final RoleMapper mapper;

    @Override
    public Role getRoleForUser(String origin) {
        return repository.findByRoleNameAndOrigin(USER_ROLE, origin)
            .map(mapper::toDomain)
            .orElseThrow(() -> new MetierException(ERROR_PROFIL_NOT_FOUND));
    }

    @Override
    public Class<RoleJpaEntity> getEntityClass() {
        return RoleJpaEntity.class;
    }
}
