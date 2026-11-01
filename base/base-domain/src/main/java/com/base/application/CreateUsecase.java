package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.CreateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUsecase<D extends BaseIdDomain> {
    private static final String ERROR_ID_MUST_BE_NULL = "ERROR.ID_MUST_BE_NULL";

    private final CreateRepositoryPort<D> createRepositoryPort;

    public D execute(D domain) {
        if (domain.getId() != null) {
            throw new MetierException(ERROR_ID_MUST_BE_NULL);
        }
        return createRepositoryPort.create(domain);
    }
}
