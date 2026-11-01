package com.base.application;

import java.time.Instant;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseCrudDomain;
import com.base.domain.port.FindByIdRepositoryPort;
import com.base.domain.port.UpdateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DesactivateUsecase<D extends BaseCrudDomain> {

    private final FindByIdRepositoryPort<D> findByIdRepositoryPort;
    private final UpdateRepositoryPort<D> updateRepositoryPort;

    public D execute(Long id) {
        if (id == null) {
            throw new MetierException(UpdateUsecase.ERROR_ID_MUST_NOT_BE_NULL);
        }
        var entity = findByIdRepositoryPort.findById(id);
        entity.setActive(false);
        entity.setDeletedDate(Instant.now());
        return updateRepositoryPort.update(entity);
    }

}
