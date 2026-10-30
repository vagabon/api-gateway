package com.base.infrastructure.adapter;

import java.time.Instant;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DesactivateRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

public interface DesactivateAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends DesactivateRepositoryPort<D>, BaseAdapter<E, D> {

    default D desactivate(Long id) {
        var entity = getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new MetierException(
                FindByIdAdapter.NOT_FOUND.formatted(id));
        }
        entity.setActive(false);
        entity.setDeletedDate(Instant.now());
        getRepository().save(entity);
        return getMapper().toDomain(entity);
    }
}
