package com.base.domain.usecase;

import java.time.Instant;
import java.util.List;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUseCase;
import com.base.domain.port.out.IBaseRepositoryPortOut;

public abstract class BaseCrudUseCase<R extends BaseDomain> extends BaseUseCase implements IBaseUseCase<R> {

    protected final IBaseRepositoryPortOut<R> repositoryPort;

    protected BaseCrudUseCase(IBaseRepositoryPortOut<R> repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public R create(R domain) {
        // U userConnected = getUserConnected();
        doBeforeCreate(domain);
        var domainCreate = repositoryPort.create(domain);
        doAfterCreate(domainCreate);
        return domainCreate;
    }

    @Override
    public R update(R domain) throws RuntimeException {
        // U userConnected = getUserConnected();
        doBeforeUpdate(domain);
        var domainUpdate = repositoryPort.update(domain);
        doAfterUpdate(domainUpdate);
        return domainUpdate;
    }

    @Override
    public boolean delete(Long id) {
        // U userConnected = getUserConnected();
        var domainBefore = repositoryPort.findById(id);
        doBeforeDelete(domainBefore);
        var domain = repositoryPort.delete(id);
        doAfterDelete(domain);
        return true;
    }

    @Override
    public R desactivate(Long id) {
        // U userConnected = getUserConnected();
        var domainBefore = repositoryPort.findById(id);
        doBeforeDelete(domainBefore);
        domainBefore.setActive(false);
        domainBefore.setDeletedDate(Instant.now());
        var domain = repositoryPort.update(domainBefore);
        doAfterDelete(domain);
        return domain;
    }

    @Override
    public List<R> findAll(Integer page, Integer max, String sort) {
        return repositoryPort.findByPage(page, max, sort);
    }

    @Override
    public R findById(Long id) {
        return repositoryPort.findById(id);
    }

    @Override
    public List<R> findBy(String fields, List<Object> fieldsValues, Integer first, Integer max) {
        return repositoryPort.findBy(fields, fieldsValues, first, max);
    }
}
