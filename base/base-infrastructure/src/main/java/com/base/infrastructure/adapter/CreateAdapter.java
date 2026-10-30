package com.base.infrastructure.adapter;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.CreateRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

public interface CreateAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends CreateRepositoryPort<D>, BaseAdapter<E, D> {

    default D create(D domain) {
        var entity = getMapper().toJpaEntity(domain);
        getRepository().save(entity);
        return getMapper().toDomain(entity);
    }
}
