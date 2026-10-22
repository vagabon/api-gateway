package com.base.infrastructure.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUseCase;
import com.base.domain.usecase.BaseCrudUseCase;

import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<R extends BaseDomain> implements IBaseUseCase<R> {

    protected final BaseCrudUseCase<R> useCase;

    protected BaseService(BaseCrudUseCase<R> useCase) {
        this.useCase = useCase;
    }

    @Override
    public String ping() {
        return useCase.ping();
    }

    @Override
    @Transactional
    public R create(R domain) {
        return useCase.create(domain);
    }

    @Override
    @Transactional
    public R update(R domain) {
        if (domain.getId() == null) {
            throw new EntityNotFoundException("id is null");
        }
        return useCase.update(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        return useCase.findById(id);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return useCase.delete(id);
    }

    @Override
    @Transactional
    public R desactivate(Long id) {
        return useCase.desactivate(id);
    }

    @Override
    public List<R> findAll(Integer page, Integer max, String sort) {
        return useCase.findAll(page, max, sort);
    }

    @Override
    public List<R> findBy(String fields, String values, Integer first, Integer max) {
        throw new UnsupportedOperationException("Unimplemented method 'findBy'");
    }
}
