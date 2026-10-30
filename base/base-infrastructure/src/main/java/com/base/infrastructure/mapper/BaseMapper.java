package com.base.infrastructure.mapper;

import com.base.domain.model.BaseIdDomain;
import com.base.infrastructure.jpa.entity.BaseIdJpaEntity;

public interface BaseMapper<E extends BaseIdJpaEntity, D extends BaseIdDomain> {
    D toDomain(E entity);

    E toJpaEntity(D domain);
}
