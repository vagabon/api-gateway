package com.base.infrastructure.adapter;

import com.base.domain.model.BaseIdDomain;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;
import com.base.infrastructure.mapper.BaseMapper;

public interface BaseAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain> {
    BaseJpaRepository<E> getRepository();

    BaseMapper<E, D> getMapper();
}
