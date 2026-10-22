package com.base.domain.usecase;

import java.time.Instant;
import java.util.List;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUserCase;
import com.base.domain.port.out.IBaseRepositoryPortOut;

public abstract class BaseCrudUseCase<R extends BaseDomain> extends BaseUseCase implements IBaseUserCase<R> {

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
    public List<R> findBy(String fields, String values, Integer first, Integer max) {
        // var userConnected = getUserConnected();
        if (first == null) {
            first = 0;
        }
        if (max == null) {
            max = 100;
        }
        return repositoryPort.constructQuery(first, max, fields, values.split(","));
    }

    // protected PageResponse<T> doAfterFindBy(U userConnected, PageResponse<T>
    // response) {
    // if (SecurityUtils.hasRole(userConnected, ADMIN)) {
    // return response;
    // }
    // return toPage(response);
    // }

    // public PageResponse<T> toPage(PageResponse<T> response) {
    // if (responseClass == null) {
    // return response;
    // }
    // return MapperUtils.toPage(response, responseClass);
    // }

    // public Response search(U userEntity, StringBuilder hqlQuery, String fields,
    // String values,
    // Integer first, Integer max) {
    // var valuesSplits = fields.split(">>");

    // hqlQuery.append("AND e.active = true ");
    // hqlQuery.append("ORDER BY e.").append(valuesSplits[1].replace("Desc", "
    // desc"));

    // var query = service.getRepository().find(hqlQuery.toString(),
    // QueryUtils.getLike(values));
    // query.page(Page.ofSize(max));
    // var response = new PageResponse<>(first, query.pageCount(), query.count(),
    // max,
    // query.page(Page.of(first, max)).list());
    // return responseOk(doAfterFindBy(userEntity, response));

    // }
}
