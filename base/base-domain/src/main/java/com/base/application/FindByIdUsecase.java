package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.FindByIdRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindByIdUsecase<D extends BaseIdDomain> {

    private final FindByIdRepositoryPort<D> findByIdRepositoryPort;

    public D execute(Long id) {
        return findByIdRepositoryPort.findById(id);
    }

}
