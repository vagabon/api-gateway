package com.base.domain.usecase;

import java.time.Instant;
import java.util.List;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUsecase;
import com.base.domain.port.out.IBaseRepositoryPortOut;

public abstract class BaseCrudUsecase<R extends BaseDomain, A extends IBaseRepositoryPortOut<R>>
    extends BaseUsecase implements IBaseUsecase<R> {

    protected final A adapter;

    protected BaseCrudUsecase(A adapter) {
        this.adapter = adapter;
    }

    @Override
    public R create(R domain) {
        // U userConnected = getUserConnected();
        doBeforeCreate(domain);
        var domainCreate = adapter.create(domain);
        doAfterCreate(domainCreate);
        return domainCreate;
    }

    @Override
    public R update(R domain) throws RuntimeException {
        // U userConnected = getUserConnected();
        doBeforeUpdate(domain);
        var domainUpdate = adapter.update(domain);
        doAfterUpdate(domainUpdate);
        return domainUpdate;
    }

    @Override
    public boolean delete(Long id) {
        // U userConnected = getUserConnected();
        var domainBefore = adapter.findById(id);
        doBeforeDelete(domainBefore);
        var domain = adapter.delete(id);
        doAfterDelete(domain);
        return true;
    }

    @Override
    public R desactivate(Long id) {
        // U userConnected = getUserConnected();
        var domainBefore = adapter.findById(id);
        doBeforeDelete(domainBefore);
        domainBefore.setActive(false);
        domainBefore.setDeletedDate(Instant.now());
        var domain = adapter.update(domainBefore);
        doAfterDelete(domain);
        return domain;
    }

    @Override
    public List<R> findAll(Integer page, Integer max, String sort) {
        return adapter.findByPage(page, max, sort);
    }

    @Override
    public R findById(Long id) {
        return adapter.findById(id);
    }

    @Override
    public List<R> findBy(
        String fields,
        List<Object> fieldsValues,
        Integer first,
        Integer max) {
        return adapter.findBy(fields, fieldsValues, first, max);
    }
}
