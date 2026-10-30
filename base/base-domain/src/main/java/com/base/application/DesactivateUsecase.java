package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DesactivateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DesactivateUsecase<D extends BaseIdDomain> {

    private final DesactivateRepositoryPort<D> desactivateRepositoryPort;

    public D execute(Long id) {
        return desactivateRepositoryPort.desactivate(id);
    }

}
