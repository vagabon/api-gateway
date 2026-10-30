package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.CreateRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateUsecase<D extends BaseIdDomain> {

    private final CreateRepositoryPort<D> createRepositoryPort;

    public D execute(D domain) {
        return createRepositoryPort.create(domain);
    }
}
