package com.base.application;

import com.base.domain.model.BaseIdDomain;
import com.base.domain.port.DeleteRepositoryPort;
import com.base.domain.port.FindByIdRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUsecase<D extends BaseIdDomain> {

    private final FindByIdRepositoryPort<D> findByIdRepositoryPort;
    private final DeleteRepositoryPort<D> deleteRepositoryPort;

    public void execute(Long id) {
        var entity = findByIdRepositoryPort.findById(id);
        deleteRepositoryPort.delete(entity);
    }
}
