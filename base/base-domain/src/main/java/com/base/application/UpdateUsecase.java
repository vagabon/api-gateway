package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.UpdateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUsecase<D extends BaseIdDomain> {
    public static final String ERROR_ID_MUST_NOT_BE_NULL = "ERROR.ID_MUST_NOT_BE_NULL";

    private final UpdateRepositoryPort<D> updateRepositoryPort;

    public D execute(D domain) {
        if (domain.getId() == null) {
            throw new MetierException(ERROR_ID_MUST_NOT_BE_NULL);
        }
        return updateRepositoryPort.update(domain);
    }
}
