package com.base.infrastructure.adapter;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DeleteRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

public interface DeleteAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends DeleteRepositoryPort, BaseAdapter<E, D> {

    default boolean delete(Long id) {
        var entity = getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new MetierException(
                FindByIdAdapter.NOT_FOUND.formatted(id));
        }
        getRepository().delete(entity);
        return true;
    }
}
