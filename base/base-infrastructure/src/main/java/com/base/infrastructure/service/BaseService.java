package com.base.infrastructure.service;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.domain.usecase.BaseCrudUseCase;

import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<R extends BaseDomain> extends BaseCrudUseCase<R> {

    protected BaseService(IBaseRepositoryPortOut<R> repositoryPort) {
        super(repositoryPort);
    }

    @Override
    @Transactional
    public R create(R domain) {
        return super.create(domain);
    }

    @Override
    @Transactional
    public R update(R domain) {
        if (domain.getId() == null) {
            throw new EntityNotFoundException("id is null");
        }
        return super.update(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return super.delete(id);
    }

    @Override
    @Transactional
    public R desactivate(Long id) {
        return super.desactivate(id);
    }
}
