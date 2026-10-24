package com.base.infrastructure.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.model.PageResult;
import com.base.domain.port.in.IBaseUsecase;
import com.base.domain.user.CurrentUser;
import com.base.infrastructure.utils.query.IQueryUtils;
import com.base.infrastructure.utils.query.QueryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<D extends BaseDomain, U extends IBaseUsecase<D>>
    implements IBaseUsecase<D>, IQueryUtils {

    protected final U usecase;

    protected BaseService(U useCase) {
        this.usecase = useCase;
    }

    @Override
    public String ping() {
        return usecase.ping();
    }

    @Override
    @Transactional
    public D create(CurrentUser userConnected, D domain) {
        return usecase.create(userConnected, domain);
    }

    @Override
    @Transactional
    public D update(CurrentUser userConnected, D domain) {
        if (domain.getId() == null) {
            throw new EntityNotFoundException("id is null");
        }
        return usecase.update(userConnected, domain);
    }

    @Override
    @Transactional
    public boolean delete(CurrentUser userConnected, Long id) {
        return usecase.delete(userConnected, id);
    }

    @Override
    @Transactional
    public D desactivate(CurrentUser userConnected, Long id) {
        return usecase.desactivate(userConnected, id);
    }

    @Override
    @Transactional(readOnly = true)
    public D findById(Long id) {
        return usecase.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<D> findAll(Integer page, Integer max, String sort) {
        return usecase.findAll(page, max, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<D> findBy(
        String fields,
        List<Object> fieldsValues,
        Integer first,
        Integer max) {

        if (first == null) {
            first = 0;
        }
        if (max == null) {
            max = 100;
        }

        var endQuery = new StringBuilder();
        var queryDtos = initQueryDto(endQuery, fields, fieldsValues.toArray());
        var sqlQuery = getSqlQuery(queryDtos, false, endQuery);
        var values = new ArrayList<Object>();
        for (var i = 0; i < fieldsValues.size(); i++) {
            var value = fieldsValues.get(i);
            if (BooleanUtils.isTrue(queryDtos.get(i).getLike())) {
                value = QueryUtils.getLike(value.toString());
            } else if ("true".equals(value)) {
                value = Boolean.TRUE;
            } else if ("false".equals(value)) {
                value = Boolean.FALSE;
            }
            values.add(value);
        }

        return usecase.findBy(sqlQuery, values, first, max);
    }
}
