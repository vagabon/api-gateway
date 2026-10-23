package com.base.infrastructure.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.port.in.IBaseUsecase;
import com.base.infrastructure.utils.query.IQueryUtils;
import com.base.infrastructure.utils.query.QueryUtils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseService<R extends BaseDomain, U extends IBaseUsecase<R>>
    implements IBaseUsecase<R>, IQueryUtils {

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
    public R create(R domain) {
        return usecase.create(domain);
    }

    @Override
    @Transactional
    public R update(R domain) {
        if (domain.getId() == null) {
            throw new EntityNotFoundException("id is null");
        }
        return usecase.update(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public R findById(Long id) {
        return usecase.findById(id);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return usecase.delete(id);
    }

    @Override
    @Transactional
    public R desactivate(Long id) {
        return usecase.desactivate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAll(Integer page, Integer max, String sort) {
        return usecase.findAll(page, max, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findBy(
        String fields,
        List<Object> fieldsValues,
        Integer first,
        Integer max) {
        // var userConnected = getUserConnected();
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
