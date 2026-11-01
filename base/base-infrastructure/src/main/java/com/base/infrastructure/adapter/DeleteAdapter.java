package com.base.infrastructure.adapter;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DeleteRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

public interface DeleteAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends DeleteRepositoryPort<D>, BaseAdapter<E, D> {

    default void delete(D domain) {
        var entity = getMapper().toJpaEntity(domain);
        getRepository().delete(entity);
    }
}
