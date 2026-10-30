package com.base.infrastructure.adapter;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.FindByIdRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

public interface FindByIdAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends FindByIdRepositoryPort<D>, BaseAdapter<E, D> {

    static final String NOT_FOUND = "ID not found : %d";

    default D findById(Long id) {
        var entity = getRepository().findById(id)
            .orElseThrow(() -> new MetierException(NOT_FOUND.formatted(id)));
        return getMapper().toDomain(entity);
    }
}
