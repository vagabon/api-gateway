package com.base.infrastructure.adapter;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.UpdateRepositoryPort;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;
import com.base.infrastructure.utils.EntityUtils;

public interface UpdateAdapter<E extends BaseCrudJpaEntity, D extends BaseIdDomain>
    extends UpdateRepositoryPort<D>, BaseAdapter<E, D> {

    default D update(D domain) {
        var entity = getMapper().toJpaEntity(domain);
        var entityFromBdd = getRepository().findById(domain.getId()).orElse(null);
        if (entityFromBdd == null) {
            throw new MetierException(
                FindByIdAdapter.NOT_FOUND.formatted(domain.getId()));
        }
        EntityUtils.setEntity(entityFromBdd, entity, false);
        var merged = getRepository().save(entityFromBdd);
        return getMapper().toDomain(merged);
    }
}
