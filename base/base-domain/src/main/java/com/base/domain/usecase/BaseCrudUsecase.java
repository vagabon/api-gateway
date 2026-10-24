package com.base.domain.usecase;

import java.time.Instant;
import java.util.List;

import com.base.domain.model.BaseDomain;
import com.base.domain.model.PageResult;
import com.base.domain.port.in.IBaseUsecase;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.domain.user.CurrentUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseCrudUsecase<D extends BaseDomain, A extends IBaseRepositoryPortOut<D>>
    extends BaseUsecase implements IBaseUsecase<D> {

    protected final A adapter;

    @Override
    public D create(CurrentUser userConnected, D domain) {
        doBeforeCreate(userConnected, domain);
        D domainCreate = adapter.create(domain);
        doAfterCreate(userConnected, domainCreate);
        return domainCreate;
    }

    @Override
    public D update(CurrentUser userConnected, D domain) throws RuntimeException {
        doBeforeUpdate(userConnected, domain);
        D domainUpdate = adapter.update(domain);
        doAfterUpdate(userConnected, domainUpdate);
        return domainUpdate;
    }

    @Override
    public boolean delete(CurrentUser userConnected, Long id) {
        D domainBefore = adapter.findById(id);
        doBeforeDelete(userConnected, domainBefore);
        D domain = adapter.delete(id);
        doAfterDelete(userConnected, domain);
        return true;
    }

    @Override
    public D desactivate(CurrentUser userConnected, Long id) {
        D domainBefore = adapter.findById(id);
        doBeforeDelete(userConnected, domainBefore);
        domainBefore.setActive(false);
        domainBefore.setDeletedDate(Instant.now());
        D domain = adapter.update(domainBefore);
        doAfterDelete(userConnected, domain);
        return domain;
    }

    @Override
    public List<D> findAll(Integer page, Integer max, String sort) {
        return adapter.findByPage(page, max, sort);
    }

    @Override
    public D findById(Long id) {
        return adapter.findById(id);
    }

    @Override
    public PageResult<D> findBy(
        String fields,
        List<Object> fieldsValues,
        Integer first,
        Integer max) {
        return adapter.findBy(fields, fieldsValues, first, max);
    }
}
