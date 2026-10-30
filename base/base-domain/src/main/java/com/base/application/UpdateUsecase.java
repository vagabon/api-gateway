package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.UpdateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUsecase<D extends BaseIdDomain> {

    private final UpdateRepositoryPort<D> updateRepositoryPort;

    public D execute(D domain) {
        return updateRepositoryPort.update(domain);
    }
}
